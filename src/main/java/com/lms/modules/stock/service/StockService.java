package com.lms.modules.stock.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.exception.BusinessException;
import com.lms.modules.book.entity.Book;
import com.lms.modules.book.service.BookService;
import com.lms.modules.stock.entity.BookStock;
import com.lms.modules.stock.mapper.BookStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class StockService {

    @Autowired
    private BookStockMapper bookStockMapper;

    @Autowired
    private BookService bookService;

    public Page<BookStock> pageQuery(Integer page, Integer size, Long bookId, String barcode) {
        Page<BookStock> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BookStock> wrapper = new LambdaQueryWrapper<>();

        if (bookId != null) {
            wrapper.eq(BookStock::getBookId, bookId);
        }
        if (barcode != null && !barcode.isEmpty()) {
            wrapper.like(BookStock::getBarcode, barcode);
        }

        wrapper.orderByDesc(BookStock::getCreateTime);
        return bookStockMapper.selectPage(pageParam, wrapper);
    }

    public List<BookStock> getByBookId(Long bookId) {
        return bookStockMapper.selectList(
                new LambdaQueryWrapper<BookStock>().eq(BookStock::getBookId, bookId)
        );
    }

    @Transactional
    public void addStock(BookStock stock) {
        // 检查图书是否存在
        Book book = bookService.getById(stock.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }

        // 生成条形码
        if (stock.getBarcode() == null || stock.getBarcode().isEmpty()) {
            stock.setBarcode(generateBarcode());
        }

        // 检查条形码是否已存在
        BookStock existStock = bookStockMapper.selectOne(
                new LambdaQueryWrapper<BookStock>().eq(BookStock::getBarcode, stock.getBarcode())
        );
        if (existStock != null) {
            throw new BusinessException("条形码已存在");
        }

        // 设置默认状态
        if (stock.getStatus() == null) {
            stock.setStatus(1); // 在馆
        }

        bookStockMapper.insert(stock);

        // 更新图书总数和可借数量
        book.setTotalQuantity(book.getTotalQuantity() + 1);
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookService.updateById(book);

        log.info("入库成功：图书ID={}, 条形码={}", stock.getBookId(), stock.getBarcode());
    }

    @Transactional
    public void updateStock(Long id, BookStock stock) {
        BookStock existStock = bookStockMapper.selectById(id);
        if (existStock == null) {
            throw new BusinessException("库存记录不存在");
        }

        stock.setId(id);
        bookStockMapper.updateById(stock);

        log.info("更新库存成功：ID={}", id);
    }

    @Transactional
    public void deleteStock(Long id) {
        BookStock stock = bookStockMapper.selectById(id);
        if (stock == null) {
            throw new BusinessException("库存记录不存在");
        }

        if (stock.getStatus() == 2) {
            throw new BusinessException("图书已借出，不能删除");
        }

        bookStockMapper.deleteById(id);

        // 更新图书总数和可借数量
        Book book = bookService.getById(stock.getBookId());
        if (book != null) {
            book.setTotalQuantity(book.getTotalQuantity() - 1);
            if (stock.getStatus() == 1) {
                book.setAvailableQuantity(book.getAvailableQuantity() - 1);
            }
            bookService.updateById(book);
        }

        log.info("删除库存成功：ID={}", id);
    }

    private String generateBarcode() {
        return "BC" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
