package com.lms.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lms.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
