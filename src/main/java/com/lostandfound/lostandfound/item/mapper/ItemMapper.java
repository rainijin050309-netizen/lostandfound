package com.lostandfound.lostandfound.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostandfound.lostandfound.item.entity.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {
}