package com.lms.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("reader")
public class Reader {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String readerNo;

    private String idCard;

    private String college;

    private String major;

    private String grade;

    private Integer borrowLimit;

    private Integer currentBorrow;

    private BigDecimal fineBalance;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String realName;
}
