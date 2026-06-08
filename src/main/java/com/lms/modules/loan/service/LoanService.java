package com.lms.modules.loan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.exception.BusinessException;
import com.lms.modules.book.entity.Book;
import com.lms.modules.book.service.BookService;
import com.lms.modules.loan.entity.LoanRecord;
import com.lms.modules.loan.mapper.LoanRecordMapper;
import com.lms.modules.user.entity.Reader;
import com.lms.modules.user.service.ReaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class LoanService {

    @Autowired
    private LoanRecordMapper loanRecordMapper;

    @Autowired
    private BookService bookService;

    @Autowired
    private ReaderService readerService;

    private static final Integer BORROW_LIMIT = 5;
    private static final Integer BORROW_DAYS = 30;
    private static final Integer RENEW_TIMES = 1;
    private static final Integer RENEW_DAYS = 15;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("0.1");

    public Page<LoanRecord> pageQuery(Integer page, Integer size, Long readerId, Integer status) {
        Page<LoanRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LoanRecord> wrapper = new LambdaQueryWrapper<>();

        if (readerId != null) {
            wrapper.eq(LoanRecord::getReaderId, readerId);
        }
        if (status != null) {
            wrapper.eq(LoanRecord::getStatus, status);
        }

        wrapper.orderByDesc(LoanRecord::getCreateTime);
        return loanRecordMapper.selectPage(pageParam, wrapper);
    }

    @Transactional
    public void createLoan(Long readerId, Long bookId) {
        // 检查读者是否存在
        Reader reader = readerService.getById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }

        // 检查读者状态
        readerService.checkReaderStatus(reader);

        // 检借阅数量
        if (reader.getCurrentBorrow() >= reader.getBorrowLimit()) {
            throw new BusinessException("已达到借阅上限");
        }

        // 检查图书是否存在
        Book book = bookService.getById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }

        // 检查图书是否有可借库存
        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessException("图书暂无可借库存");
        }

        // 检查读者是否有逾期未还
        Long overdueCount = loanRecordMapper.selectCount(
                new LambdaQueryWrapper<LoanRecord>()
                        .eq(LoanRecord::getReaderId, readerId)
                        .eq(LoanRecord::getStatus, 2)
        );
        if (overdueCount > 0) {
            throw new BusinessException("有图书逾期未还，请先归还");
        }

        // 创建借阅记录
        LoanRecord loanRecord = new LoanRecord();
        loanRecord.setReaderId(readerId);
        loanRecord.setBookId(bookId);
        loanRecord.setLoanDate(LocalDateTime.now());
        loanRecord.setDueDate(LocalDateTime.now().plusDays(BORROW_DAYS));
        loanRecord.setRenewCount(0);
        loanRecord.setFine(BigDecimal.ZERO);
        loanRecord.setStatus(0); // 借阅中

        loanRecordMapper.insert(loanRecord);

        // 更新图书可借数量
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookService.updateById(book);

        // 更新读者当前借阅数量
        reader.setCurrentBorrow(reader.getCurrentBorrow() + 1);
        readerService.updateById(reader);

        log.info("借阅成功：读者ID={}, 图书ID={}", readerId, bookId);
    }

    @Transactional
    public void returnBook(Long loanId) {
        LoanRecord loanRecord = loanRecordMapper.selectById(loanId);
        if (loanRecord == null) {
            throw new BusinessException("借阅记录不存在");
        }

        if (loanRecord.getStatus() == 1) {
            throw new BusinessException("图书已归还");
        }

        // 计算是否逾期
        LocalDateTime now = LocalDateTime.now();
        BigDecimal fine = BigDecimal.ZERO;

        if (now.isAfter(loanRecord.getDueDate())) {
            // 计算逾期天数
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(loanRecord.getDueDate(), now);
            fine = FINE_PER_DAY.multiply(new BigDecimal(overdueDays));
        }

        // 更新借阅记录
        loanRecord.setReturnDate(now);
        loanRecord.setFine(fine);
        loanRecord.setStatus(fine.compareTo(BigDecimal.ZERO) > 0 ? 2 : 1); // 有罚款为逾期，否则已归还
        loanRecordMapper.updateById(loanRecord);

        // 更新图书可借数量
        Book book = bookService.getById(loanRecord.getBookId());
        if (book != null) {
            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookService.updateById(book);
        }

        // 更新读者当前借阅数量和罚款余额
        Reader reader = readerService.getById(loanRecord.getReaderId());
        if (reader != null) {
            reader.setCurrentBorrow(reader.getCurrentBorrow() - 1);
            reader.setFineBalance(reader.getFineBalance().add(fine));
            readerService.updateById(reader);
        }

        log.info("归还成功：借阅ID={}, 罚款={}", loanId, fine);
    }

    @Transactional
    public void renewBook(Long loanId) {
        LoanRecord loanRecord = loanRecordMapper.selectById(loanId);
        if (loanRecord == null) {
            throw new BusinessException("借阅记录不存在");
        }

        if (loanRecord.getStatus() != 0) {
            throw new BusinessException("只能续借借阅中的图书");
        }

        if (loanRecord.getRenewCount() >= RENEW_TIMES) {
            throw new BusinessException("已达到续借次数上限");
        }

        // 更新应还日期
        loanRecord.setDueDate(loanRecord.getDueDate().plusDays(RENEW_DAYS));
        loanRecord.setRenewCount(loanRecord.getRenewCount() + 1);
        loanRecordMapper.updateById(loanRecord);

        log.info("续借成功：借阅ID={}", loanId);
    }

    public Page<LoanRecord> getOverdueList(Integer page, Integer size) {
        Page<LoanRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LoanRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanRecord::getStatus, 2); // 逾期
        wrapper.orderByDesc(LoanRecord::getCreateTime);
        return loanRecordMapper.selectPage(pageParam, wrapper);
    }
}
