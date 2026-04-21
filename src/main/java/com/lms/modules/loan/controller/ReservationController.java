package com.lms.modules.loan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.result.Result;
import com.lms.modules.loan.entity.Reservation;
import com.lms.modules.loan.service.ReservationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public Result<Page<Reservation>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long readerId,
            @RequestParam(required = false) Long bookId
    ) {
        Page<Reservation> result = reservationService.pageQuery(page, size, readerId, bookId);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> create(@RequestBody ReservationRequest request) {
        reservationService.createReservation(request.getReaderId(), request.getBookId());
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return Result.success();
    }

    @Data
    public static class ReservationRequest {
        private Long readerId;
        private Long bookId;
    }
}
