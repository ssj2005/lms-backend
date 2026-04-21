package com.lms.modules.stock.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.result.Result;
import com.lms.modules.stock.entity.BookStock;
import com.lms.modules.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public Result<Page<BookStock>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String barcode
    ) {
        Page<BookStock> result = stockService.pageQuery(page, size, bookId, barcode);
        return Result.success(result);
    }

    @GetMapping("/books/{bookId}")
    public Result<List<BookStock>> getByBookId(@PathVariable Long bookId) {
        List<BookStock> result = stockService.getByBookId(bookId);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> add(@RequestBody BookStock stock) {
        stockService.addStock(stock);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody BookStock stock) {
        stockService.updateStock(id, stock);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        stockService.deleteStock(id);
        return Result.success();
    }
}
