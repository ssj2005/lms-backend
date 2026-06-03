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

    @GetMapping("/by-user/{userId}")
    public Result<Reader> getByUserId(@PathVariable Long userId) {
        Reader reader = readerService.getByUserId(userId);
        return Result.success(reader);
    }

    @GetMapping("/unlinked-users")
    public Result<java.util.List<com.lms.modules.user.entity.User>> getUnlinkedUsers() {
        return Result.success(readerService.getUnlinkedUsers());
    }

    @PostMapping("/{id}/report-loss")
    public Result<Void> reportLoss(@PathVariable Long id) {
        readerService.reportLoss(id);
        return Result.success();
    }

    @PostMapping("/{id}/approve-loss")
    public Result<Void> approveLoss(@PathVariable Long id) {
        readerService.approveLoss(id);
        return Result.success();
    }

    @PostMapping("/{id}/reject-loss")
    public Result<Void> rejectLoss(@PathVariable Long id) {
        readerService.rejectLoss(id);
        return Result.success();
    }

    @PostMapping("/{id}/unsuspend")
    public Result<Void> unsuspend(@PathVariable Long id) {
        readerService.unsuspend(id);
        return Result.success();
    }

    @PostMapping("/{id}/approve-unsuspend")
    public Result<Void> approveUnsuspend(@PathVariable Long id) {
        readerService.approveUnsuspend(id);
        return Result.success();
    }

    @PostMapping("/{id}/admin-unsuspend")
    public Result<Void> adminUnsuspend(@PathVariable Long id) {
        readerService.adminUnsuspend(id);
        return Result.success();
    }
}
