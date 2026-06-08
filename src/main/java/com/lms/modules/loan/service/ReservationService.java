package com.lms.modules.loan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.exception.BusinessException;
import com.lms.modules.book.entity.Book;
import com.lms.modules.book.service.BookService;
import com.lms.modules.loan.entity.Reservation;
import com.lms.modules.loan.mapper.ReservationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private BookService bookService;

    @Autowired
    private com.lms.modules.user.service.ReaderService readerService;

    private static final Integer RESERVE_DAYS = 3;

    public Page<Reservation> pageQuery(Integer page, Integer size, Long readerId, Long bookId) {
        Page<Reservation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();

        if (readerId != null) {
            wrapper.eq(Reservation::getReaderId, readerId);
        }
        if (bookId != null) {
            wrapper.eq(Reservation::getBookId, bookId);
        }

        wrapper.orderByDesc(Reservation::getCreateTime);
        return reservationMapper.selectPage(pageParam, wrapper);
    }

    @Transactional
    public void createReservation(Long readerId, Long bookId) {
        // 检查读者是否存在
        com.lms.modules.user.entity.Reader reader = readerService.getById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }

        // 检查读者状态
        readerService.checkReaderStatus(reader);

        // 检查图书是否存在
        Book book = bookService.getById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }

        // 检查是否已预约
        Long existCount = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getReaderId, readerId)
                        .eq(Reservation::getBookId, bookId)
                        .in(Reservation::getStatus, 0, 1) // 等待中或已到书
        );
        if (existCount > 0) {
            throw new BusinessException("已预约该书，请勿重复预约");
        }

        // 检查图书是否有可借库存
        if (book.getAvailableQuantity() > 0) {
            throw new BusinessException("图书有可借库存，无需预约");
        }

        // 创建预约记录
        Reservation reservation = new Reservation();
        reservation.setReaderId(readerId);
        reservation.setBookId(bookId);
        reservation.setReserveDate(LocalDateTime.now());
        reservation.setExpireDate(LocalDateTime.now().plusDays(RESERVE_DAYS));
        reservation.setStatus(0); // 等待中

        reservationMapper.insert(reservation);

        log.info("预约成功：读者ID={}, 图书ID={}", readerId, bookId);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }

        if (reservation.getStatus() != 0) {
            throw new BusinessException("只能取消等待中的预约");
        }

        reservation.setStatus(2); // 已取消
        reservationMapper.updateById(reservation);

        log.info("取消预约成功：预约ID={}", reservationId);
    }

    // 检查并更新过期预约
    public void checkExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();

        // 查找所有等待中且已过期的预约
        reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getStatus, 0)
                        .lt(Reservation::getExpireDate, now)
        ).forEach(reservation -> {
            reservation.setStatus(3); // 已过期
            reservationMapper.updateById(reservation);
        });
    }
}
