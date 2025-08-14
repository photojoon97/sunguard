package com.joon.sunguard_api.global.publicapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.joon.sunguard_api.global.exception.CustomException;
import com.joon.sunguard_api.global.exception.ErrorCode;
import com.joon.sunguard_api.global.publicapi.WrapperResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class PublicApiFetcher {
    private final WebClient webClient;
    private final XmlMapper xmlMapper;

    public PublicApiFetcher(WebClient webClient){
        this.webClient = webClient;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public <T> WrapperResponse<T> fetchXmlToWrapper(String api_url, String api_key, Object request, TypeReference<WrapperResponse<T>> typeReference){
        Map<String, String>paramMap = xmlMapper.convertValue(request, Map.class);
        paramMap.put("serviceKey", api_key);

        try{
            // API를 호출하여 XML 응답을 문자열로 받습니다.
            String xmlResponse = webClient.get()
                    .uri(api_url, uriBuilder -> {
                        paramMap.forEach(uriBuilder::queryParam);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .doOnSuccess(response -> log.debug("Raw XML response : {}", response))
                    .doOnError(error -> log.error("API 호출 실패. URL: {}, Params: {}", api_url, paramMap, error))
                    .onErrorMap(error -> new CustomException(ErrorCode.API_CALL_ERROR, error.getMessage()))
                    .block();

            if (xmlResponse == null || xmlResponse.trim().isEmpty()){
                log.warn("API response 비어있음. URL: {}, Params: {}", api_url, paramMap);
                throw new CustomException(ErrorCode.API_CALL_ERROR);
            }

                return xmlMapper.readValue(xmlResponse, typeReference);
        }catch (JsonProcessingException e){
            log.error("XML 파싱 실패. Error: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.XML_PARSE_ERROR, e.getMessage());
        }
    }
}