package com.lostandfound.lostandfound.item.controller;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.item.dto.ItemCreateDTO;
import com.lostandfound.lostandfound.item.dto.ItemQueryDTO;
import com.lostandfound.lostandfound.item.service.ItemService;
import com.lostandfound.lostandfound.item.vo.ItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Result<ItemVO> createItem(@RequestBody ItemCreateDTO dto) {
        return itemService.createItem(dto);
    }

    @GetMapping("/{id}")
    public Result<ItemVO> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/list")
    public Result<List<ItemVO>> queryItems(ItemQueryDTO dto) {
        return itemService.queryItems(dto);
    }

    @PutMapping("/{id}/status")
    public Result<ItemVO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return itemService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        return itemService.deleteItem(id);
    }
}
