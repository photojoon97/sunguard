package com.joon.sunguard_api.global.publicapi;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenApiCallContext {

    private final Map<String, OpenApiCallStrategy> strategies;

    public OpenApiCallContext(Map<String, OpenApiCallStrategy> strategies){
        this.strategies = strategies;
    }

    public <T, R> Object excute(String strategyName, String key, String url, R requestParam, TypeReference<WrapperResponse<T>> typeReference){
        OpenApiCallStrategy strategy = strategies.get(strategyName);
        if(strategy == null){
            throw  new IllegalArgumentException("strategy를 찾을 수 없음 : " +  strategyName);
        }
        return strategy.callApi(key, url, requestParam, typeReference);
    }
}