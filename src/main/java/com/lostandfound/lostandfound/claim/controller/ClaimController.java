package com.lostandfound.lostandfound.claim.controller;

import com.lostandfound.lostandfound.claim.dto.ClaimCreateDTO;
import com.lostandfound.lostandfound.claim.service.ClaimService;
import com.lostandfound.lostandfound.claim.vo.ClaimVO;
import com.lostandfound.lostandfound.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    public Result<ClaimVO> createClaim(@RequestBody ClaimCreateDTO dto) {
        return claimService.createClaim(dto);
    }

    @GetMapping("/item/{itemId}")
    public Result<List<ClaimVO>> getClaimsByItemId(@PathVariable Long itemId) {
        return claimService.getClaimsByItemId(itemId);
    }

    @GetMapping("/user/{userId}")
    public Result<List<ClaimVO>> getClaimsByUserId(@PathVariable Long userId) {
        return claimService.getClaimsByUserId(userId);
    }

    @PutMapping("/{id}/status")
    public Result<ClaimVO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return claimService.updateStatus(id, status);
    }
}
