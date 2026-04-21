package com.lms.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.modules.system.entity.Notice;
import com.lms.modules.system.mapper.NoticeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public Page<Notice> pageQuery(Integer page, Integer size, Integer type, Integer status) {
        Page<Notice> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        if (type != null) {
            wrapper.eq(Notice::getType, type);
        }
        if (status != null) {
            wrapper.eq(Notice::getStatus, status);
        }

        wrapper.orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectPage(pageParam, wrapper);
    }

    public Notice getById(Long id) {
        return noticeMapper.selectById(id);
    }

    public void save(Notice notice) {
        noticeMapper.insert(notice);
    }

    public void updateById(Notice notice) {
        noticeMapper.updateById(notice);
    }

    public void deleteById(Long id) {
        noticeMapper.deleteById(id);
    }

    public List<Notice> getPublishedNotices() {
        return noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>()
                        .eq(Notice::getStatus, 1)
                        .orderByDesc(Notice::getCreateTime)
                        .last("LIMIT 5")
        );
    }
}
