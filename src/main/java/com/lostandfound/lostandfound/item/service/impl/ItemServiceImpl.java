package com.lostandfound.lostandfound.item.service.impl;

import com.lostandfound.lostandfound.claim.mapper.ClaimMapper;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final ClaimMapper claimMapper;

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
        int offset = (page - 1) * size;

        List<Item> items = itemMapper.findByCondition(
                dto.getType(),
                dto.getCategory(),
                dto.getStatus(),
                dto.getKeyword(),
                offset,
                size
        );

        List<ItemVO> list = items.stream()
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
        claimMapper.deleteByItemId(id);
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
        User user = userMapper.selectById(item.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }
}