package com.example.interceptiontext.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.example.interceptiontext.domain.ExtractConfig;
import com.example.interceptiontext.service.TextExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 文字截取逻辑层
 *
 * @author xiafan
 * @time 2023/10/09 17:15
 */
@Service
@Slf4j
public class TextExtractServiceImpl implements TextExtractService {

    /**
     * 文字截取逻辑处理
     *
     * @param response 响应体
     * @param files 上传文件
     * @param extractForm  截取配置
     */
    @Override
    public void textExtractHandle(MultipartFile[] files, String extractForm, HttpServletResponse response) {
        ExtractConfig extractConfig = JSONObject.parseObject(extractForm, ExtractConfig.class);
        //处理上传文件，生成截取文字集合。
        List<String> list = filesHandle(files, extractConfig);
        if (list.size() > 0){
            //对截取文字集合根据截取文字某位置进行排序。
            sort(list,extractConfig);
        }
        //导出文件。
        exportFiles(list,response);
    }

    /**
     * 导出文件。
     *
     * @param list 截取文字集合
     * @param response  响应体
     */
    public void exportFiles(List<String> list, HttpServletResponse response){
        try {
            ExcelWriter writer = ExcelUtil.getWriter(true);
            writer.write(list, true);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("截取文字", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            ServletOutputStream out=response.getOutputStream();
            writer.flush(out, true);
            //关闭wirter，释放内存
            writer.close();
            //关机输出servlet流
            IoUtil.close(out);
        } catch (IOException e) {
            log.info("导出文字截取时出现异常！",e);
        }
    }

    /**
     * 对截取文字集合根据截取文字某位置进行排序。
     *
     * @param list 截取文字集合
     * @param extractConfig  截取配置
     */
    public void sort(List<String> list, ExtractConfig extractConfig){
        Boolean sortOrNot = extractConfig.getSortOrNot();
        if (sortOrNot){
            if (StringUtils.equals(extractConfig.getSortOrder(), "1")){
                Collections.sort(list, (a1, a2) -> {
                    int io1 = Integer.parseInt(a1.substring(0,1));
                    int io2 = Integer.parseInt(a2.substring(0,1));
                    return io2 - io1;
                });
            }
            if (StringUtils.equals(extractConfig.getSortOrder(), "2")){
                Collections.sort(list, (a1, a2) -> {
                    int io1 = Integer.parseInt(a1.substring(a1.length() -1));
                    int io2 = Integer.parseInt(a2.substring(a2.length() -1));
                    return io2 - io1;
                });
            }
            if (StringUtils.equals(extractConfig.getSortOrder(), "3")){
                int sortPosition = Integer.parseInt(extractConfig.getSortPosition());
                Collections.sort(list, (a1, a2) -> {
                    int io1 = Integer.parseInt(a1.substring(sortPosition-1,sortPosition));
                    int io2 = Integer.parseInt(a2.substring(sortPosition-1,sortPosition));
                    return io2 - io1;
                });
            }
        }
    }

    /**
     * 处理上传文件，生成截取文字集合。
     *
     * @param files 上传文件
     * @param extractConfig  截取配置
     * @return List<String>
     */
    public List<String> filesHandle(MultipartFile[] files, ExtractConfig extractConfig){
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        try {
            for (MultipartFile file: files) {
                // 将MultipartFile转换为BufferedReader
                BufferedReader bufferedReader = convert(file);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    //截取开始文字
                    String startingText = extractConfig.getStartingText();
                    //截取文字位置
                    int startingTextIndex = line.indexOf(startingText);
                    if (startingTextIndex > -1){
                        //截取开始位置
                        String startingPosition = extractConfig.getStartingPosition();
                        //截取结束位置
                        String endPosition = extractConfig.getEndPosition();
                        //截取文字
                        String extractText = line.substring(startingTextIndex + Integer.parseInt(startingPosition),
                                startingTextIndex + Integer.parseInt(endPosition));
                        set.add(extractText);
                    }
                }
                bufferedReader.close();
            }
            list = new ArrayList<>(set);
        } catch (IOException e){
            log.info("处理文字截取时出现异常！",e);
        }
        return list;
    }

    public static BufferedReader convert(MultipartFile multipartFile) throws IOException {
        InputStreamReader isr = new InputStreamReader(multipartFile.getInputStream(), StandardCharsets.UTF_8);
        return new BufferedReader(isr);
    }
}
