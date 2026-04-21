package com.lms.modules.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.result.Result;
import com.lms.modules.user.entity.Reader;
import com.lms.modules.user.service.ReaderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/readers")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @GetMapping
    public Result<Page<Reader>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String readerNo,
            @RequestParam(required = false) String college
    ) {
        Page<Reader> result = readerService.pageQuery(page, size, readerNo, college);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Reader> getById(@PathVariable Long id) {
        Reader reader = readerService.getById(id);
        return Result.success(reader);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Reader reader) {
        readerService.save(reader);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Reader reader) {
        reader.setId(id);
        readerService.updateById(reader);
        return Result.success();
    }
}
