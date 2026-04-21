package com.lms.modules.book.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.modules.book.entity.Book;
import com.lms.modules.book.mapper.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    public Page<Book> pageQuery(Integer page, Integer size, String title, String author, String isbn) {
        Page<Book> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();

        if (title != null && !title.isEmpty()) {
            wrapper.like(Book::getTitle, title);
        }
        if (author != null && !author.isEmpty()) {
            wrapper.like(Book::getAuthor, author);
        }
        if (isbn != null && !isbn.isEmpty()) {
            wrapper.eq(Book::getIsbn, isbn);
        }

        return bookMapper.selectPage(pageParam, wrapper);
    }

    public Book getById(Long id) {
        return bookMapper.selectById(id);
    }

    public void save(Book book) {
        book.setAvailableQuantity(book.getTotalQuantity());
        bookMapper.insert(book);
    }

    public void updateById(Book book) {
        bookMapper.updateById(book);
    }

    public void deleteById(Long id) {
        bookMapper.deleteById(id);
    }
}
