package com.lostandfound.lostandfound.claim.service;

import com.lostandfound.lostandfound.claim.dto.ClaimCreateDTO;
import com.lostandfound.lostandfound.claim.vo.ClaimVO;
import com.lostandfound.lostandfound.common.Result;

import java.util.List;

public interface ClaimService {
    Result<ClaimVO> createClaim(ClaimCreateDTO dto);
    Result<List<ClaimVO>> getClaimsByItemId(Long itemId);
    Result<List<ClaimVO>> getClaimsByUserId(Long userId);
    Result<ClaimVO> updateStatus(Long id, String status);
}