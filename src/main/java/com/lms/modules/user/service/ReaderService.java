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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReaderService {

    @Autowired
    private ReaderMapper readerMapper;

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        Page<Reader> result = readerMapper.selectPage(pageParam, wrapper);

        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            Set<Long> userIds = result.getRecords().stream()
                    .map(Reader::getUserId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!userIds.isEmpty()) {
                List<User> users = userMapper.selectBatchIds(userIds);
                Map<Long, User> userMap = users.stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
                for (Reader r : result.getRecords()) {
                    User u = userMap.get(r.getUserId());
                    if (u != null) {
                        r.setRealName(u.getRealName());
                        r.setUsername(u.getUsername());
                    }
                }
            }
        }

        return result;
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

        if (reader.getUserId() != null) {
            User user = userMapper.selectById(reader.getUserId());
            if (user == null) {
                throw new BusinessException("关联用户不存在");
            }
        } else {
            if (reader.getUsername() == null || reader.getUsername().isEmpty()) {
                throw new BusinessException("请输入用户名");
            }
            if (reader.getRealName() == null || reader.getRealName().isEmpty()) {
                throw new BusinessException("请输入姓名");
            }

            User existUser = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, reader.getUsername())
            );
            if (existUser != null) {
                throw new BusinessException("用户名已存在");
            }

            User newUser = new User();
            newUser.setUsername(reader.getUsername());
            newUser.setRealName(reader.getRealName());
            newUser.setPassword(passwordEncoder.encode(reader.getUsername()));
            newUser.setRole(0);
            newUser.setStatus(1);
            userMapper.insert(newUser);

            reader.setUserId(newUser.getId());
        }

        if (reader.getBorrowLimit() == null) {
            reader.setBorrowLimit(5);
        }
        if (reader.getCurrentBorrow() == null) {
            reader.setCurrentBorrow(0);
        }
        if (reader.getFineBalance() == null) {
            reader.setFineBalance(java.math.BigDecimal.ZERO);
        }
        if (reader.getStatus() == null) {
            reader.setStatus(0);
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

    public List<User> getUnlinkedUsers() {
        return userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, 0)
                        .notInSql(User::getId, "SELECT user_id FROM reader WHERE user_id IS NOT NULL")
        );
    }

    public void checkReaderStatus(Reader reader) {
        if (reader.getStatus() != null && reader.getStatus() != 0) {
            throw new BusinessException("读者账号已挂失，无法操作");
        }
    }

    @Transactional
    public void reportLoss(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (reader.getStatus() != null && reader.getStatus() != 0) {
            throw new BusinessException("读者已处于挂失流程中");
        }

        reader.setStatus(1);
        readerMapper.updateById(reader);

        User user = userMapper.selectById(reader.getUserId());
        if (user != null) {
            user.setStatus(0);
            userMapper.updateById(user);
        }

        log.info("读者挂失申请：读者ID={}", readerId);
    }

    @Transactional
    public void approveLoss(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (reader.getStatus() != 1) {
            throw new BusinessException("读者未申请挂失");
        }

        reader.setStatus(2);
        readerMapper.updateById(reader);

        log.info("挂失审核通过：读者ID={}", readerId);
    }

    @Transactional
    public void rejectLoss(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (reader.getStatus() != 1) {
            throw new BusinessException("读者未申请挂失");
        }

        reader.setStatus(0);
        readerMapper.updateById(reader);

        User user = userMapper.selectById(reader.getUserId());
        if (user != null) {
            user.setStatus(1);
            userMapper.updateById(user);
        }

        log.info("挂失申请驳回：读者ID={}", readerId);
    }

    @Transactional
    public void unsuspend(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (reader.getStatus() != 2) {
            throw new BusinessException("读者未被挂失，无需解挂");
        }

        reader.setStatus(3);
        readerMapper.updateById(reader);

        log.info("读者申请解挂：读者ID={}", readerId);
    }

    @Transactional
    public void approveUnsuspend(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (reader.getStatus() != 3) {
            throw new BusinessException("未检测到解挂申请");
        }

        reader.setStatus(0);
        readerMapper.updateById(reader);

        User user = userMapper.selectById(reader.getUserId());
        if (user != null) {
            user.setStatus(1);
            userMapper.updateById(user);
        }

        log.info("解挂审核通过：读者ID={}", readerId);
    }

    @Transactional
    public void adminUnsuspend(Long readerId) {
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (reader.getStatus() != 2) {
            throw new BusinessException("当前读者状态未处于已挂失，无需解挂");
        }

        reader.setStatus(0);
        readerMapper.updateById(reader);

        User user = userMapper.selectById(reader.getUserId());
        if (user != null) {
            user.setStatus(1);
            userMapper.updateById(user);
        }

        log.info("管理员直接解挂：读者ID={}", readerId);
    }
}
