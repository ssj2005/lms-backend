package com.lms.modules.loan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lms.modules.loan.entity.LoanRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanRecordMapper extends BaseMapper<LoanRecord> {
}
