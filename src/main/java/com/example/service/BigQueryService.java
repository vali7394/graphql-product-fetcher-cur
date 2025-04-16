package com.example.service;

import com.example.config.AppProperties;
import com.example.model.ProductSearchResponse;
import com.example.model.Product;
import com.example.model.SearchEngine;
import com.example.model.PartType;
import com.example.model.ProductSearchResult;
import com.google.cloud.bigquery.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BigQueryService {
    private final AppProperties appProperties;
    private final BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
    private final Gson gson = new Gson();

    public void writeSearchResults(ProductSearchResponse response) {
        String datasetId = appProperties.getBigquery().getDataset();
        String tableId = appProperties.getBigquery().getTable();

        try {
            TableId tableRef = TableId.of(datasetId, tableId);
            Table table = bigQuery.getTable(tableRef);

            if (table == null) {
                createTable(datasetId, tableId);
            }

            List<InsertAllRequest.RowToInsert> rows = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

            // Process each product in the response
            ProductSearchResult searchResult = response.getProducts();
            List<Product> products = searchResult.getProducts();
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                SearchEngine searchEngine = searchResult.getSearchEngine();
                
                InsertAllRequest.RowToInsert row = InsertAllRequest.RowToInsert.of(
                    Map.of(
                        "id", UUID.randomUUID().toString(),
                        "search_query", searchEngine.getSearchQuery(),
                        "category", searchEngine.getEntity(),
                        "product_id", product.getId(),
                        "product_info", gson.toJson(product),
                        "position", i + 1,
                        "is_applicated", searchResult.isSuccess(),
                        "env", "dev", // TODO: Make this configurable in AppProperties
                        "application_part_types", product.getPartType() != null ? 
                            List.of(product.getPartType().getName()) : new ArrayList<>(),
                        "relevancy_score", product.getViews(),
                        "load_date", now.toString(),
                        "channel", "web", // Default value, can be made configurable
                        "country", product.getCountry(),
                        "language", "en", // Default value, can be made configurable
                        "filter_info", gson.toJson(searchResult.getFacets())
                    )
                );
                rows.add(row);
            }

            InsertAllResponse insertResponse = bigQuery.insertAll(
                InsertAllRequest.newBuilder(tableRef)
                    .setRows(rows)
                    .build()
            );

            if (insertResponse.hasErrors()) {
                log.error("Error inserting rows into BigQuery: {}", insertResponse.getInsertErrors());
                throw new RuntimeException("Failed to insert rows into BigQuery");
            }

            log.info("Successfully wrote {} search results to BigQuery table: {}.{}", 
                rows.size(), datasetId, tableId);
            
        } catch (Exception e) {
            log.error("Error writing to BigQuery dataset: {}, table: {}", datasetId, tableId, e);
            throw new RuntimeException("Failed to write to BigQuery", e);
        }
    }

    private void createTable(String datasetId, String tableId) {
        Schema schema = Schema.of(
            Field.of("id", StandardSQLTypeName.STRING),
            Field.of("search_query", StandardSQLTypeName.STRING),
            Field.of("category", StandardSQLTypeName.STRING),
            Field.of("product_id", StandardSQLTypeName.STRING),
            Field.of("product_info", StandardSQLTypeName.JSON),
            Field.of("position", StandardSQLTypeName.INT64),
            Field.of("is_applicated", StandardSQLTypeName.BOOL),
            Field.of("env", StandardSQLTypeName.STRING),
            Field.newBuilder("application_part_types", StandardSQLTypeName.STRING)
                .setMode(Field.Mode.REPEATED)
                .build(),
            Field.of("relevancy_score", StandardSQLTypeName.INT64),
            Field.of("load_date", StandardSQLTypeName.DATETIME),
            Field.of("channel", StandardSQLTypeName.STRING),
            Field.of("country", StandardSQLTypeName.STRING),
            Field.of("language", StandardSQLTypeName.STRING),
            Field.of("filter_info", StandardSQLTypeName.JSON)
        );

        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(TableId.of(datasetId, tableId), tableDefinition)
            .setTimePartitioning(TimePartitioning.of(TimePartitioning.Type.DAY))
            .build();

        bigQuery.create(tableInfo);
        log.info("Created BigQuery table: {}.{}", datasetId, tableId);
    }
} 