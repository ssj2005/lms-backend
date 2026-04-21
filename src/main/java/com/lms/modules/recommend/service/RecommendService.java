package com.lms.modules.recommend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lms.modules.book.entity.Book;
import com.lms.modules.book.mapper.BookMapper;
import com.lms.modules.loan.entity.LoanRecord;
import com.lms.modules.loan.mapper.LoanRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private LoanRecordMapper loanRecordMapper;

    public List<Map<String, Object>> getRecommendations(Long readerId) {
        // 获取读者的借阅历史
        List<LoanRecord> loanRecords = loanRecordMapper.selectList(
                new LambdaQueryWrapper<LoanRecord>()
                        .eq(LoanRecord::getReaderId, readerId)
                        .orderByDesc(LoanRecord::getLoanDate)
                        .last("LIMIT 10")
        );

        // 提取借阅过的图书分类
        Set<String> borrowedCategories = new HashSet<>();
        Set<Long> borrowedBookIds = new HashSet<>();

        for (LoanRecord record : loanRecords) {
            Book book = bookMapper.selectById(record.getBookId());
            if (book != null) {
                if (book.getCategory() != null) {
                    borrowedCategories.add(book.getCategory());
                }
                borrowedBookIds.add(book.getId());
            }
        }

        // 推荐算法：基于借阅历史的分类推荐
        List<Book> allBooks = bookMapper.selectList(new LambdaQueryWrapper<>());

        // 过滤掉已借阅的图书
        List<Book> candidateBooks = allBooks.stream()
                .filter(book -> !borrowedBookIds.contains(book.getId()))
                .filter(book -> book.getAvailableQuantity() > 0) // 只推荐可借的图书
                .collect(Collectors.toList());

        // 计算推荐分数
        Map<Book, Double> bookScores = new HashMap<>();
        for (Book book : candidateBooks) {
            double score = 0.0;

            // 如果分类匹配，增加分数
            if (book.getCategory() != null && borrowedCategories.contains(book.getCategory())) {
                score += 10.0;
            }

            // 根据可借数量调整分数
            score += book.getAvailableQuantity() * 0.5;

            // 根据出版日期调整分数（新书优先）
            if (book.getPublishDate() != null) {
                long daysSincePublish = java.time.temporal.ChronoUnit.DAYS.between(
                        book.getPublishDate(), java.time.LocalDate.now()
                );
                if (daysSincePublish < 365) {
                    score += 5.0; // 一年内的新书
                }
            }

            bookScores.put(book, score);
        }

        // 排序并返回前10个推荐
        return bookScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    Book book = entry.getKey();
                    result.put("id", book.getId());
                    result.put("title", book.getTitle());
                    result.put("author", book.getAuthor());
                    result.put("cover", book.getCoverUrl());
                    result.put("category", book.getCategory());
                    result.put("score", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }
}
