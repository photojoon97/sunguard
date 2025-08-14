package com.joon.sunguard_api.domain.route.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransferInfo {
    private String transferNodeName;
    private String transferNodeId;

    private String fromLineNum; // 내린 버스
    private String fromLineId;

    private String toLineNum;   // 탈 버스
    private String toLineId;
}
