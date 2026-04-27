package com.lostandfound.lostandfound.item.service;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.item.dto.ItemCreateDTO;
import com.lostandfound.lostandfound.item.dto.ItemQueryDTO;
import com.lostandfound.lostandfound.item.vo.ItemVO;

import java.util.List;

public interface ItemService {
    Result<ItemVO> createItem(ItemCreateDTO dto);
    Result<ItemVO> getItemById(Long id);
    Result<List<ItemVO>> queryItems(ItemQueryDTO dto);
    Result<ItemVO> updateStatus(Long id, String status);
    Result<Void> deleteItem(Long id);
}