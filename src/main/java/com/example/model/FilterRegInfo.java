package com.example.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class FilterRegInfo {
    private String[] filters;
} 