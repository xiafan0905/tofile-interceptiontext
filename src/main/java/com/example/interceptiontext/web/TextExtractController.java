package com.example.interceptiontext.web;

import com.example.interceptiontext.service.TextExtractService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xiafan
 * @Description: 从文件中截取部分文字的功能
 * @time 2023/10/09 17:15
 */
@Controller
@RequestMapping("learn")
public class TextExtractController {

    @Resource
    private TextExtractService textExtractService;

    /**
     * html模板入口
     * @return 'resources/templates'下的html模板
     */
    @GetMapping("/textExtract")
    public String textExtractPage() {
        return "textExtract/textExtract";
    }

    /**
     * 从文件中截取部分文字的功能
     *
     * @param response 响应体
     * @param files 上传文件
     * @param extractForm  截取配置
     */
    @PostMapping("/textExtractHandle")
    public void textExtractHandle(HttpServletResponse response, @RequestParam("files") MultipartFile[] files,
                                  @RequestParam("extractForm") String extractForm) {
        textExtractService.textExtractHandle(files, extractForm, response);
    }
}
