package com.lms.modules.book.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.result.Result;
import com.lms.modules.book.entity.Book;
import com.lms.modules.book.service.BookService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public Result<Page<Book>> search(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn
    ) {
        Page<Book> result = bookService.pageQuery(page, size, title, author, isbn);
        return Result.success(result);
    }

    @GetMapping
    public Result<Page<Book>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<Book> result = bookService.pageQuery(page, size, null, null, null);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Long id) {
        Book book = bookService.getById(id);
        return Result.success(book);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Book book) {
        bookService.save(book);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.updateById(book);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.deleteById(id);
        return Result.success();
    }
}
