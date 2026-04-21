package com.lms.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.result.Result;
import com.lms.modules.system.entity.Notice;
import com.lms.modules.system.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public Result<Page<Notice>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status
    ) {
        Page<Notice> result = noticeService.pageQuery(page, size, type, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        return Result.success(notice);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Notice notice) {
        noticeService.save(notice);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        noticeService.updateById(notice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/published")
    public Result<List<Notice>> getPublishedNotices() {
        List<Notice> result = noticeService.getPublishedNotices();
        return Result.success(result);
    }
}
