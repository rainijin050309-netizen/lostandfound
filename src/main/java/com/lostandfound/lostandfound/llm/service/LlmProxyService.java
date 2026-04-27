package com.lostandfound.lostandfound.llm.service;

import com.lostandfound.lostandfound.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class LlmProxyService {

    @Value("${llm.proxy.url:http://127.0.0.1:5000/llm-query}")
    private String llmProxyUrl;

    @Value("${llm.proxy.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${llm.proxy.read-timeout-ms:15000}")
    private int readTimeoutMs;

    public Result<Map<String, Object>> query(String queryText) {
        if (!StringUtils.hasText(queryText)) {
            return Result.fail("query不能为空");
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        RestTemplate restTemplate = new RestTemplate(factory);
        Map<String, String> payload = Map.of("query", queryText.trim());

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = restTemplate.postForObject(llmProxyUrl, payload, Map.class);
            if (body == null) {
                return Result.fail("LLM服务返回为空");
            }
            return Result.success(body);
        } catch (RestClientException e) {
            log.error("调用LLM代理失败", e);
            return Result.fail("LLM服务暂不可用，请稍后重试");
        }
    }
}
