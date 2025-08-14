package com.joon.sunguard_api.global.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;


import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "response")
public class WrapperResponse<T> {

    @JsonProperty("header")
    private Header header;

    @JsonProperty("body")
    private Body<T> body;

    @Getter
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Getter
    public static class Body<T> {
        @JsonProperty("items")
        private Items<T> items;

        @JsonProperty("numOfRows")
        private Integer numOfRows;

        @JsonProperty("pageNo")
        private Integer pageNo;

        @JsonProperty("totalCount")
        private Integer totalCount;
    }

    @Getter
    public static class Items<T> {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("item")
        private List<T> item;
    }

    public T getSingleItem() {
        if (body != null && body.getItems() != null) {
            List<T> itemList = body.getItems().getItem();
            return itemList != null && !itemList.isEmpty() ? itemList.get(0) : null;
        }
        return null;
    }

    public List<T> getItemList() {
        if (body != null && body.getItems() != null) {
            return body.getItems().getItem();
        }
        return null;
    }
}
