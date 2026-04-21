package com.lms.modules.statistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lms.modules.book.mapper.BookMapper;
import com.lms.modules.loan.mapper.LoanRecordMapper;
import com.lms.modules.user.mapper.ReaderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class StatisticsService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ReaderMapper readerMapper;

    @Autowired
    private LoanRecordMapper loanRecordMapper;

    public Map<String, Object> getDailyStats(LocalDate date) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

        // 今日借阅数量
        Long todayLoans = loanRecordMapper.selectCount(
                new LambdaQueryWrapper<com.lms.modules.loan.entity.LoanRecord>()
                        .between(com.lms.modules.loan.entity.LoanRecord::getLoanDate, startOfDay, endOfDay)
        );

        // 今日归还数量
        Long todayReturns = loanRecordMapper.selectCount(
                new LambdaQueryWrapper<com.lms.modules.loan.entity.LoanRecord>()
                        .between(com.lms.modules.loan.entity.LoanRecord::getReturnDate, startOfDay, endOfDay)
        );

        // 当前借阅总数
        Long totalLoans = loanRecordMapper.selectCount(
                new LambdaQueryWrapper<com.lms.modules.loan.entity.LoanRecord>()
                        .eq(com.lms.modules.loan.entity.LoanRecord::getStatus, 0)
        );

        // 逾期数量
        Long overdueCount = loanRecordMapper.selectCount(
                new LambdaQueryWrapper<com.lms.modules.loan.entity.LoanRecord>()
                        .eq(com.lms.modules.loan.entity.LoanRecord::getStatus, 2)
        );

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("todayLoans", todayLoans);
        result.put("todayReturns", todayReturns);
        result.put("totalLoans", totalLoans);
        result.put("overdueCount", overdueCount);

        return result;
    }

    public List<Map<String, Object>> getMonthlyStats(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        List<Map<String, Object>> result = new ArrayList<>();

        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            result.add(getDailyStats(date));
        }

        return result;
    }

    public List<Map<String, Object>> getPopularBooks(Integer limit) {
        // 获取借阅次数最多的前N本图书
        List<com.lms.modules.loan.entity.LoanRecord> loans = loanRecordMapper.selectList(
                new LambdaQueryWrapper<com.lms.modules.loan.entity.LoanRecord>()
                        .orderByDesc(com.lms.modules.loan.entity.LoanRecord::getBookId)
                        .last("LIMIT " + (limit != null ? limit : 10))
        );

        Map<Long, Integer> bookLoanCount = new HashMap<>();
        for (com.lms.modules.loan.entity.LoanRecord loan : loans) {
            bookLoanCount.put(loan.getBookId(), bookLoanCount.getOrDefault(loan.getBookId(), 0) + 1);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : bookLoanCount.entrySet()) {
            com.lms.modules.book.entity.Book book = bookMapper.selectById(entry.getKey());
            if (book != null) {
                Map<String, Object> bookInfo = new HashMap<>();
                bookInfo.put("id", book.getId());
                bookInfo.put("title", book.getTitle());
                bookInfo.put("author", book.getAuthor());
                bookInfo.put("cover", book.getCoverUrl());
                bookInfo.put("loanCount", entry.getValue());
                result.add(bookInfo);
            }
        }

        // 按借阅次数排序
        result.sort((a, b) -> ((Integer) b.get("loanCount")).compareTo((Integer) a.get("loanCount")));

        return result.stream().limit(limit != null ? limit : 10).toList();
    }

    public List<Map<String, Object>> getActiveReaders(Integer limit) {
        List<com.lms.modules.loan.entity.LoanRecord> loans = loanRecordMapper.selectList(
                new LambdaQueryWrapper<com.lms.modules.loan.entity.LoanRecord>()
                        .orderByDesc(com.lms.modules.loan.entity.LoanRecord::getReaderId)
        );

        Map<Long, Integer> readerLoanCount = new HashMap<>();
        for (com.lms.modules.loan.entity.LoanRecord loan : loans) {
            readerLoanCount.put(loan.getReaderId(), readerLoanCount.getOrDefault(loan.getReaderId(), 0) + 1);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : readerLoanCount.entrySet()) {
            com.lms.modules.user.entity.Reader reader = readerMapper.selectById(entry.getKey());
            if (reader != null) {
                Map<String, Object> readerInfo = new HashMap<>();
                readerInfo.put("id", reader.getId());
                readerInfo.put("readerNo", reader.getReaderNo());
                readerInfo.put("loanCount", entry.getValue());
                result.add(readerInfo);
            }
        }

        result.sort((a, b) -> ((Integer) b.get("loanCount")).compareTo((Integer) a.get("loanCount")));

        return result.stream().limit(limit != null ? limit : 10).toList();
    }
}
