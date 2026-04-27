package com.lostandfound.lostandfound.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostandfound.lostandfound.item.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {

    List<Item> findByCondition(
            @Param("type") String type,
            @Param("category") String category,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("size") int size
    );

    long countByCondition(
            @Param("type") String type,
            @Param("category") String category,
            @Param("status") String status,
            @Param("keyword") String keyword
    );
}