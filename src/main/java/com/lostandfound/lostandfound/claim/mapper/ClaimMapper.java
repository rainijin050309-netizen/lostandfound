package com.lostandfound.lostandfound.claim.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostandfound.lostandfound.claim.entity.Claim;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClaimMapper extends BaseMapper<Claim> {

    List<Claim> findByItemId(@Param("itemId") Long itemId);

    List<Claim> findByUserId(@Param("userId") Long userId);

    long countByItemIdAndUserId(
            @Param("itemId") Long itemId,
            @Param("userId") Long userId
    );

    void rejectOtherClaims(
            @Param("itemId") Long itemId,
            @Param("excludeId") Long excludeId
    );

    void deleteByItemId(@Param("itemId") Long itemId);
}