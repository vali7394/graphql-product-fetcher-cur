package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class SearchEngine {
    private String searchQuery;
    private String entity;
    private List<String> matchedCategories;
} 