package com.lostandfound.lostandfound.llm.controller;

import com.lostandfound.lostandfound.common.Result;
import com.lostandfound.lostandfound.llm.dto.LlmQueryRequest;
import com.lostandfound.lostandfound.llm.service.LlmProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LlmController {

    private final LlmProxyService llmProxyService;

    @PostMapping("/llm-query")
    public Result<Map<String, Object>> llmQuery(@RequestBody LlmQueryRequest request) {
        return llmProxyService.query(request.getQuery());
    }
}
