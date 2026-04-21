package com.lms.modules.statistics.controller;

import com.lms.common.result.Result;
import com.lms.modules.statistics.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/daily")
    public Result<Map<String, Object>> getDailyStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        Map<String, Object> result = statisticsService.getDailyStats(date);
        return Result.success(result);
    }

    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStats(
            @RequestParam(defaultValue = "2026") Integer year,
            @RequestParam(defaultValue = "1") Integer month
    ) {
        List<Map<String, Object>> result = statisticsService.getMonthlyStats(year, month);
        return Result.success(result);
    }

    @GetMapping("/popular-books")
    public Result<List<Map<String, Object>>> getPopularBooks(
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<Map<String, Object>> result = statisticsService.getPopularBooks(limit);
        return Result.success(result);
    }

    @GetMapping("/active-readers")
    public Result<List<Map<String, Object>>> getActiveReaders(
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<Map<String, Object>> result = statisticsService.getActiveReaders(limit);
        return Result.success(result);
    }
}
