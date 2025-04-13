package com.example.service;

import com.example.model.ClientInfo;
import com.example.model.PaginationRegInfo;
import com.example.model.ProductSearchRequest;
import com.example.model.SortingRegInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final GcsService gcsService;
    private final GraphQLService graphQLService;
    private final BigQueryService bigQueryService;
    private final Executor virtualThreadExecutor;

    @Async("virtualThreadExecutor")
    public CompletableFuture<Void> processKeywords() {
        List<String> keywords = gcsService.readKeywords();
        log.info("Processing {} keywords", keywords.size());

        List<CompletableFuture<Void>> futures = keywords.stream()
            .map(this::processKeyword)
            .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Void> processKeyword(String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            ProductSearchRequest request = createSearchRequest(keyword);
            return graphQLService.searchProducts(request);
        }, virtualThreadExecutor)
        .thenCompose(responseFuture -> responseFuture)
        .thenAccept(response -> {
            bigQueryService.writeSearchResults(response);
            log.info("Processed keyword: {}", keyword);
        })
        .exceptionally(throwable -> {
            log.error("Error processing keyword: {}", keyword, throwable);
            return null;
        });
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