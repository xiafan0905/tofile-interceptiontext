package com.example.interceptiontext.domain;

import lombok.Data;

@Data
public class ExtractConfig {

    //截取文字
    private String startingText;
    //是否排序
    private Boolean sortOrNot;
    //排序方式
    private String sortOrder;
    //排序位置
    private String sortPosition;
    //开始位置
    private String startingPosition;
    //结束位置
    private String endPosition;
}
