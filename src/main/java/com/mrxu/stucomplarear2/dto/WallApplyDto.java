package com.mrxu.stucomplarear2.dto;

import lombok.Data;

@Data
public class WallApplyDto {
    /**
     * 墙内容
     */
    private String wallContent;

    /**
     * 申请人ID
     */
    private Integer userId;

}
