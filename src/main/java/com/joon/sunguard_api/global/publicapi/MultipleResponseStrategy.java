package com.joon.sunguard_api.global.publicapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.joon.sunguard_api.global.publicapi.util.PublicApiFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("multipleResponseStrategy")
@RequiredArgsConstructor
public class MultipleResponseStrategy implements OpenApiCallStrategy {

    private final PublicApiFetcher publicApiFetcher;

    @Override
    public <T,R> Object callApi(String key, String url, R request, TypeReference<WrapperResponse<T>> typeReference) {

        try {
            WrapperResponse<T> result = publicApiFetcher.fetchXmlToWrapper(url, key, request, typeReference);
            List<T> itemList = result.getItemList();

            if (itemList != null && !itemList.isEmpty()) {
                log.info("API 호출 성공. Fetched {} items.", itemList.size());
                itemList.forEach(item -> log.debug("Fetched item: {}", item.toString()));
            } else {
                log.warn("API 호출 응답 없음.");
            }

            return itemList;

        } catch (Exception e) {
            log.error("Xml -> Dto List 변환 실패", e);
            throw new RuntimeException("Xml -> Dto List 변환 실패 : " + e.getMessage());
        }
    }
}