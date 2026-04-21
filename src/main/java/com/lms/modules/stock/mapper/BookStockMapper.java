package com.lms.modules.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lms.modules.stock.entity.BookStock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookStockMapper extends BaseMapper<BookStock> {
}
