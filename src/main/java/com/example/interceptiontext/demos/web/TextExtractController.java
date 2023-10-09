package com.example.interceptiontext.demos.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author 夏帆
 * @Description: 从文件中截取部分文字的功能
 * @time 2023/10/09 17:15
 */
@Controller
@RequestMapping("learn")
public class TextExtractController {

    /**
     * html模板入口
     * @return 'resources/templates'下的html模板
     */
    @GetMapping("/textExtract")
    public String textExtract() {
        return "textExtract/textExtract";
    }
}
