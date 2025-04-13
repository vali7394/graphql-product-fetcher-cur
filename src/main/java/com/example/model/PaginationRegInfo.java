package com.example.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class PaginationRegInfo {
    private int page;
    private int pageSize;
} 