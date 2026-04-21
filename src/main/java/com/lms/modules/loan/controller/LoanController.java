package com.lms.modules.loan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.result.Result;
import com.lms.modules.loan.entity.LoanRecord;
import com.lms.modules.loan.service.LoanService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping
    public Result<Page<LoanRecord>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long readerId,
            @RequestParam(required = false) Integer status
    ) {
        Page<LoanRecord> result = loanService.pageQuery(page, size, readerId, status);
        return Result.success(result);
    }

    @GetMapping("/overdue")
    public Result<Page<LoanRecord>> overdueList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<LoanRecord> result = loanService.getOverdueList(page, size);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> create(@RequestBody LoanRequest request) {
        loanService.createLoan(request.getReaderId(), request.getBookId());
        return Result.success();
    }

    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        loanService.returnBook(id);
        return Result.success();
    }

    @PutMapping("/{id}/renew")
    public Result<Void> renew(@PathVariable Long id) {
        loanService.renewBook(id);
        return Result.success();
    }

    @Data
    public static class LoanRequest {
        private Long readerId;
        private Long bookId;
    }
}
