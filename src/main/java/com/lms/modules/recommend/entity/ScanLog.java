package com.lms.modules.recommend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scan_log")
public class ScanLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long readerId;

    private Long bookId;

    private Integer scanType;

    private String barcode;

    private String operator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
