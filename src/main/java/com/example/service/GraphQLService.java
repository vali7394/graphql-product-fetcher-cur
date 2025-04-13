package com.example.service;

import com.example.config.AppProperties;
import com.example.model.ProductSearchRequest;
import com.example.model.ProductSearchResponse;
import com.netflix.graphql.dgs.client.GraphQLClient;
import com.netflix.graphql.dgs.client.GraphQLResponse;
import com.netflix.graphql.dgs.client.HttpResponse;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphQLService {
    private final AppProperties appProperties;
    private final RestTemplate restTemplate;
    private final RateLimiter rateLimiter;

    public GraphQLService(AppProperties appProperties, RestTemplate restTemplate) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplate;
        
        // Configure rate limiter
        int rateLimit = appProperties.getGraphql().getRateLimitPerSec();
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(rateLimit)
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .build();
        this.rateLimiter = RateLimiter.of("graphql-rate-limiter", config);
    }

    public CompletableFuture<ProductSearchResponse> searchProducts(ProductSearchRequest request) {
        return CompletableFuture.supplyAsync(() -> 
            RateLimiter.decorateSupplier(rateLimiter, () -> {
                try {
                    GraphQLClient client = GraphQLClient.createCustom(
                        appProperties.getGraphql().getUrl(),
                        (url, body, headers) -> {
                            HttpHeaders httpHeaders = new HttpHeaders();
                            if (appProperties.getGraphql().getHeaders().getAuthorization() != null) {
                                httpHeaders.setBearerAuth(appProperties.getGraphql().getHeaders().getAuthorization());
                            }
                            
                            return new HttpResponse(
                                200,
                                restTemplate.postForObject(url, body, String.class),
                                httpHeaders
                            );
                        }
                    );

                    String query = """
                        query ProductSearch(
                            $phrase: String,
                            $clientInfo: ClientInfo!,
                            $facetFilter: [String],
                            $filterRegInfo: FilterRegInfo,
                            $paginationRegInfo: PaginationRegInfo,
                            $sortingRegInfo: SortingRegInfo,
                            $semanticSearch: Boolean
                        ) {
                            products: productSearch(input: {
                                phrase: $phrase,
                                clientInfo: $clientInfo,
                                facetFilter: $facetFilter,
                                filterRegInfo: $filterRegInfo,
                                paginationRegInfo: $paginationRegInfo,
                                sortingRegInfo: $sortingRegInfo,
                                semanticSearch: $semanticSearch
                            }) {
                                success
                                numFound
                                recognizedVehicles {
                                    make { id value }
                                    model { id value }
                                    remainingPhrase
                                    vehicleType { id value }
                                    year
                                }
                                metadata {
                                    applicationSpecific {
                                        queryClassification {
                                            recognizedAttributes
                                        }
                                    }
                                }
                                searchEngine {
                                    searchQuery
                                    entity
                                    matchedCategories
                                }
                                facets {
                                    name displayName filterPlace open
                                    values { value count }
                                }
                                products {
                                    id applicationTitle productAttributes {
                                        specialAttributes {
                                            value attributeName
                                        }
                                    }
                                    brand country status mainNumber oem products storeTitle lineObservation time views
                                    quality partType { name }
                                }
                            }
                        }
                        """;

                    GraphQLResponse response = client.executeQuery(
                        query,
                        Map.of(
                            "phrase", request.getPhrase(),
                            "clientInfo", request.getClientInfo(),
                            "facetFilter", request.getFacetFilter(),
                            "filterRegInfo", request.getFilterRegInfo(),
                            "paginationRegInfo", request.getPaginationRegInfo(),
                            "sortingRegInfo", request.getSortingRegInfo(),
                            "semanticSearch", request.getSemanticSearch()
                        )
                    );

                    return response.extractValueAsObject("products", ProductSearchResponse.class);
                    
                } catch (Exception e) {
                    log.error("Error executing GraphQL query for phrase: {}", request.getPhrase(), e);
                    throw new RuntimeException("Failed to execute GraphQL query", e);
                }
            }).get()
        );
    }
} 