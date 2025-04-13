package com.example.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ClientInfo {
    private String clientId;
    private String clientVersion;
} 