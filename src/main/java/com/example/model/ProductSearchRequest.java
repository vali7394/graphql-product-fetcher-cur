package com.example.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProductSearchRequest {
    private String phrase;
    private ClientInfo clientInfo;
    private String[] facetFilter;
    private FilterRegInfo filterRegInfo;
    private PaginationRegInfo paginationRegInfo;
    private SortingRegInfo sortingRegInfo;
    private Boolean semanticSearch;
} 