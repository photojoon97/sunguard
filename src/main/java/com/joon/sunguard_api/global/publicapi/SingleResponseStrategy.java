package com.joon.sunguard_api.global.publicapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.joon.sunguard_api.global.publicapi.util.PublicApiFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("singleResponseStrategy")
@RequiredArgsConstructor
public class SingleResponseStrategy implements OpenApiCallStrategy {

    private final PublicApiFetcher publicApiFetcher;

    @Override
    public <T, R> Object callApi(String key, String url, R request, TypeReference<WrapperResponse<T>> typeReference) {
        try {
            WrapperResponse<T> result = publicApiFetcher.fetchXmlToWrapper(url, key, request, typeReference);
            List<T> itemList = result.getItemList();

            if (itemList != null && !itemList.isEmpty()) {
                T firstItem = itemList.get(0);
                log.info("API 호출 성공. Fetched single item: {}", firstItem.toString());
                return firstItem;
            } else {
                log.warn("API가 반환하는 데이터 없음.");
                return null;
            }
        } catch (Exception e) {
            log.error("Xml -> Dto 변환 실패", e);
            throw new RuntimeException("Xml -> Dto 변환 실패 : " + e.getMessage());
        }
    }
}