package com.example.interceptiontext.service;

import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

/**
 * 文字截取service
 *
 * @author xiafan
 * @time 2023/10/09 17:15
 */
public interface TextExtractService {

    /**
     * 文字截取逻辑处理
     *
     * @param response 响应体
     * @param files 上传文件
     * @param extractForm  截取配置
     */
    void textExtractHandle(MultipartFile[] files, String extractForm, HttpServletResponse response);
}
