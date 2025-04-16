package com.example.service;

import com.example.model.ClientInfo;
import com.example.model.PaginationRegInfo;
import com.example.model.ProductSearchRequest;
import com.example.model.ProductSearchResponse;
import com.example.model.SortingRegInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final GcsService gcsService;
    private final GraphQLService graphQLService;
    private final BigQueryService bigQueryService;

    public void processKeywords() {
        List<String> keywords = gcsService.readKeywords();
        log.info("Processing {} keywords", keywords.size());

        for (String keyword : keywords) {
            try {
                processKeyword(keyword);
                log.info("Processed keyword: {}", keyword);
            } catch (Exception e) {
                log.error("Error processing keyword: {}", keyword, e);
            }
        }
    }

    private void processKeyword(String keyword) {
        ProductSearchRequest request = createSearchRequest(keyword);
        ProductSearchResponse response = graphQLService.searchProducts(request);
        bigQueryService.writeSearchResults(response);
    }

    private ProductSearchRequest createSearchRequest(String keyword) {
        return ProductSearchRequest.builder()
            .phrase(keyword)
            .clientInfo(ClientInfo.builder()
                .clientId("product-search-service")
                .clientVersion("1.0.0")
                .build())
            .paginationRegInfo(PaginationRegInfo.builder()
                .page(1)
                .pageSize(100)
                .build())
            .sortingRegInfo(SortingRegInfo.builder()
                .sortBy("relevance")
                .sortOrder("desc")
                .build())
            .semanticSearch(true)
            .build();
    }
} 