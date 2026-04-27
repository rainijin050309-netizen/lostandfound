package com.lostandfound.lostandfound.stats.service.impl;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.stats.mapper.StatsMapper;
import com.lostandfound.lostandfound.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsMapper statsMapper;

    @Override
    public Result<List<Map<String, Object>>> categoryStats() {
        return Result.success(statsMapper.selectCategoryStats());
    }

    @Override
    public Result<List<Map<String, Object>>> locationStats() {
        return Result.success(statsMapper.selectLocationStats());
    }

    @Override
    public Result<List<Map<String, Object>>> monthlyStats() {
        return Result.success(statsMapper.selectMonthlyStats());
    }

    @Override
    public Result<Map<String, Object>> overviewStats() {
        return Result.success(statsMapper.selectOverviewStats());
    }
}
