package com.lms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计数据DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {

    /**
     * 总图书数
     */
    private Long totalBooks;

    /**
     * 总读者数
     */
    private Long totalReaders;

    /**
     * 总借阅数（当前正在借阅的数量）
     */
    private Long totalLoans;

    /**
     * 逾期数量
     */
    private Long overdueCount;

    /**
     * 今日借阅数
     */
    private Long todayLoans;

    /**
     * 今日归还数
     */
    private Long todayReturns;
}
