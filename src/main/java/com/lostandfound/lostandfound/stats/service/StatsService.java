package com.lostandfound.lostandfound.stats.service;

import com.lostandfound.lostandfound.common.Result;

import java.util.List;
import java.util.Map;

public interface StatsService {

    Result<List<Map<String, Object>>> categoryStats();

    Result<List<Map<String, Object>>> locationStats();

    Result<List<Map<String, Object>>> monthlyStats();

    Result<Map<String, Object>> overviewStats();
}
