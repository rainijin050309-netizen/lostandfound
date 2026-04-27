package com.lostandfound.lostandfound.stats.controller;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/category")
    public Result<List<Map<String, Object>>> category() {
        return statsService.categoryStats();
    }

    @GetMapping("/location")
    public Result<List<Map<String, Object>>> location() {
        return statsService.locationStats();
    }

    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> monthly() {
        return statsService.monthlyStats();
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return statsService.overviewStats();
    }
}
