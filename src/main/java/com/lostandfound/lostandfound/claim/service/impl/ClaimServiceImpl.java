package com.lostandfound.lostandfound.claim.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lostandfound.lostandfound.claim.dto.ClaimCreateDTO;
import com.lostandfound.lostandfound.claim.entity.Claim;
import com.lostandfound.lostandfound.claim.mapper.ClaimMapper;
import com.lostandfound.lostandfound.claim.service.ClaimService;
import com.lostandfound.lostandfound.claim.vo.ClaimVO;
import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.item.entity.Item;
import com.lostandfound.lostandfound.item.mapper.ItemMapper;
import com.lostandfound.lostandfound.user.entity.User;
import com.lostandfound.lostandfound.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimMapper claimMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public Result<ClaimVO> createClaim(ClaimCreateDTO dto) {
        // 检查物品是否存在
        Item item = itemMapper.selectById(dto.getItemId());
        if (item == null) {
            return Result.fail("物品不存在");
        }
        if (!"open".equals(item.getStatus())) {
            return Result.fail("该物品已关闭认领");
        }
        // 检查是否已经认领过
        Long count = claimMapper.selectCount(
                new LambdaQueryWrapper<Claim>()
                        .eq(Claim::getItemId, dto.getItemId())
                        .eq(Claim::getUserId, dto.getUserId())
        );
        if (count > 0) {
            return Result.fail("你已经提交过认领申请");
        }
        Claim claim = new Claim();
        claim.setItemId(dto.getItemId());
        claim.setUserId(dto.getUserId());
        claim.setMessage(dto.getMessage());
        claim.setStatus("pending");
        claimMapper.insert(claim);
        return Result.success(toVO(claim));
    }

    @Override
    public Result<List<ClaimVO>> getClaimsByItemId(Long itemId) {
        List<Claim> claims = claimMapper.selectList(
                new LambdaQueryWrapper<Claim>()
                        .eq(Claim::getItemId, itemId)
                        .orderByDesc(Claim::getCreatedAt)
        );
        return Result.success(claims.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    public Result<List<ClaimVO>> getClaimsByUserId(Long userId) {
        List<Claim> claims = claimMapper.selectList(
                new LambdaQueryWrapper<Claim>()
                        .eq(Claim::getUserId, userId)
                        .orderByDesc(Claim::getCreatedAt)
        );
        return Result.success(claims.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Override
    public Result<ClaimVO> updateStatus(Long id, String status) {
        Claim claim = claimMapper.selectById(id);
        if (claim == null) {
            return Result.fail("认领记录不存在");
        }
        claim.setStatus(status);
        claimMapper.updateById(claim);

        // 如果审批通过，把物品状态改为claimed
        if ("approved".equals(status)) {
            Item item = itemMapper.selectById(claim.getItemId());
            if (item != null) {
                item.setStatus("claimed");
                itemMapper.updateById(item);
            }
        }
        return Result.success(toVO(claim));
    }

    private ClaimVO toVO(Claim claim) {
        ClaimVO vo = new ClaimVO();
        vo.setId(claim.getId());
        vo.setItemId(claim.getItemId());
        vo.setUserId(claim.getUserId());
        vo.setMessage(claim.getMessage());
        vo.setStatus(claim.getStatus());
        vo.setCreatedAt(claim.getCreatedAt());

        Item item = itemMapper.selectById(claim.getItemId());
        if (item != null) {
            vo.setItemTitle(item.getTitle());
        }
        User user = userMapper.selectById(claim.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }
}