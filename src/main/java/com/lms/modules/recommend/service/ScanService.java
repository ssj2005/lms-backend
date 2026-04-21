package com.lms.modules.recommend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lms.modules.book.entity.Book;
import com.lms.modules.book.mapper.BookMapper;
import com.lms.modules.stock.entity.BookStock;
import com.lms.modules.stock.mapper.BookStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ScanService {

    @Autowired
    private BookStockMapper bookStockMapper;

    @Autowired
    private BookMapper bookMapper;

    public Map<String, Object> scanBarcode(String barcode) {
        // 查询库存记录
        BookStock stock = bookStockMapper.selectOne(
                new LambdaQueryWrapper<BookStock>().eq(BookStock::getBarcode, barcode)
        );

        if (stock == null) {
            throw new com.lms.common.exception.BusinessException("条形码不存在");
        }

        // 查询图书信息
        Book book = bookMapper.selectById(stock.getBookId());
        if (book == null) {
            throw new com.lms.common.exception.BusinessException("图书信息不存在");
        }

        // 返回扫码结果
        Map<String, Object> result = new HashMap<>();
        result.put("bookId", book.getId());
        result.put("stockId", stock.getId());
        result.put("title", book.getTitle());
        result.put("author", book.getAuthor());
        result.put("isbn", book.getIsbn());
        result.put("barcode", stock.getBarcode());
        result.put("location", stock.getLocation());
        result.put("status", stock.getStatus());
        result.put("availableQuantity", book.getAvailableQuantity());
        result.put("scanTime", LocalDateTime.now());

        log.info("扫码成功：条形码={}, 图书={}", barcode, book.getTitle());

        return result;
    }
}
