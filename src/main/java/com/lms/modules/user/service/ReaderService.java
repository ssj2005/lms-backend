package com.lms.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.common.exception.BusinessException;
import com.lms.modules.user.entity.Reader;
import com.lms.modules.user.entity.User;
import com.lms.modules.user.mapper.ReaderMapper;
import com.lms.modules.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ReaderService {

    @Autowired
    private ReaderMapper readerMapper;

    @Autowired
    private UserMapper userMapper;

    public Page<Reader> pageQuery(Integer page, Integer size, String readerNo, String college) {
        Page<Reader> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<>();

        if (readerNo != null && !readerNo.isEmpty()) {
            wrapper.like(Reader::getReaderNo, readerNo);
        }
        if (college != null && !college.isEmpty()) {
            wrapper.like(Reader::getCollege, college);
        }

        wrapper.orderByDesc(Reader::getCreateTime);
        return readerMapper.selectPage(pageParam, wrapper);
    }

    public Reader getById(Long id) {
        return readerMapper.selectById(id);
    }

    @Transactional
    public void save(Reader reader) {
        // 检查读者证号是否已存在
        Reader existReader = readerMapper.selectOne(
                new LambdaQueryWrapper<Reader>().eq(Reader::getReaderNo, reader.getReaderNo())
        );
        if (existReader != null) {
            throw new BusinessException("读者证号已存在");
        }

        // 验证用户是否存在
        User user = userMapper.selectById(reader.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 设置默认值
        if (reader.getBorrowLimit() == null) {
            reader.setBorrowLimit(5);
        }
        if (reader.getCurrentBorrow() == null) {
            reader.setCurrentBorrow(0);
        }
        if (reader.getFineBalance() == null) {
            reader.setFineBalance(java.math.BigDecimal.ZERO);
        }

        readerMapper.insert(reader);
    }

    @Transactional
    public void updateById(Reader reader) {
        readerMapper.updateById(reader);
    }

    public List<Reader> getAllReaders() {
        return readerMapper.selectList(new LambdaQueryWrapper<>());
    }

    public Reader getByUserId(Long userId) {
        return readerMapper.selectOne(
                new LambdaQueryWrapper<Reader>().eq(Reader::getUserId, userId)
        );
    }
}
