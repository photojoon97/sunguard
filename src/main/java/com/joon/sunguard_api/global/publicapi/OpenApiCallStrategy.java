package com.joon.sunguard_api.global.publicapi;

import com.fasterxml.jackson.core.type.TypeReference;

public interface OpenApiCallStrategy {
   <T, R> Object callApi(String key, String url, R request, TypeReference<WrapperResponse<T>> typeReference);
}