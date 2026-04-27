package com.lostandfound.lostandfound.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.item.dto.ItemCreateDTO;
import com.lostandfound.lostandfound.item.dto.ItemQueryDTO;
import com.lostandfound.lostandfound.item.entity.Item;
import com.lostandfound.lostandfound.item.mapper.ItemMapper;
import com.lostandfound.lostandfound.item.service.ItemService;
import com.lostandfound.lostandfound.item.vo.ItemVO;
import com.lostandfound.lostandfound.user.entity.User;
import com.lostandfound.lostandfound.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public Result<ItemVO> createItem(ItemCreateDTO dto) {
        Item item = new Item();
        item.setUserId(dto.getUserId());
        item.setType(dto.getType());
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setCategory(dto.getCategory());
        item.setLocation(dto.getLocation());
        item.setLostDate(dto.getLostDate());
        item.setFoundDate(dto.getFoundDate());
        item.setImageUrl(dto.getImageUrl());
        item.setStatus("open");
        itemMapper.insert(item);
        return Result.success(toVO(item));
    }

    @Override
    public Result<ItemVO> getItemById(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            return Result.fail("物品不存在");
        }
        return Result.success(toVO(item));
    }

    @Override
    public Result<List<ItemVO>> queryItems(ItemQueryDTO dto) {
        int page = dto.getPage() != null ? dto.getPage() : 1;
        int size = dto.getSize() != null ? dto.getSize() : 10;

        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getType())) {
            wrapper.eq(Item::getType, dto.getType());
        }
        if (StringUtils.hasText(dto.getCategory())) {
            wrapper.eq(Item::getCategory, dto.getCategory());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(Item::getStatus, dto.getStatus());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(Item::getTitle, dto.getKeyword())
                    .or().like(Item::getDescription, dto.getKeyword()));
        }
        wrapper.orderByDesc(Item::getCreatedAt);

        IPage<Item> pageResult = itemMapper.selectPage(new Page<>(page, size), wrapper);
        List<ItemVO> list = pageResult.getRecords()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Override
    public Result<ItemVO> updateStatus(Long id, String status) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            return Result.fail("物品不存在");
        }
        item.setStatus(status);
        itemMapper.updateById(item);
        return Result.success(toVO(item));
    }

    @Override
    public Result<Void> deleteItem(Long id) {
        itemMapper.deleteById(id);
        return Result.success();
    }

    private ItemVO toVO(Item item) {
        ItemVO vo = new ItemVO();
        vo.setId(item.getId());
        vo.setUserId(item.getUserId());
        vo.setType(item.getType());
        vo.setTitle(item.getTitle());
        vo.setDescription(item.getDescription());
        vo.setCategory(item.getCategory());
        vo.setLocation(item.getLocation());
        vo.setLostDate(item.getLostDate());
        vo.setFoundDate(item.getFoundDate());
        vo.setImageUrl(item.getImageUrl());
        vo.setStatus(item.getStatus());
        vo.setCreatedAt(item.getCreatedAt());

        // 查询发布者用户名
        User user = userMapper.selectById(item.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }
}