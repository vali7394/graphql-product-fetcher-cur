package com.example.service;

import com.example.config.AppProperties;
import com.example.model.ProductSearchResponse;
import com.google.cloud.bigquery.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BigQueryService {
    private final AppProperties appProperties;
    private final BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();

    public void writeSearchResults(ProductSearchResponse response) {
        String datasetId = appProperties.getBigquery().getDataset();
        String tableId = appProperties.getBigquery().getTable();

        try {
            TableId tableRef = TableId.of(datasetId, tableId);
            Table table = bigQuery.getTable(tableRef);

            if (table == null) {
                createTable(datasetId, tableId);
            }

            List<FieldValue> row = new ArrayList<>();
            row.add(FieldValue.of(FieldValue.Attribute.PRIMITIVE, UUID.randomUUID().toString()));
            row.add(FieldValue.of(FieldValue.Attribute.PRIMITIVE, response.getProducts().getNumFound()));
            row.add(FieldValue.of(FieldValue.Attribute.PRIMITIVE, response.getProducts().getSuccess()));
            
            // Add more fields as needed based on your BigQuery schema
            
            InsertAllResponse insertResponse = bigQuery.insertAll(
                InsertAllRequest.newBuilder(tableRef)
                    .addRow(row)
                    .build()
            );

            if (insertResponse.hasErrors()) {
                log.error("Error inserting rows into BigQuery: {}", insertResponse.getInsertErrors());
                throw new RuntimeException("Failed to insert rows into BigQuery");
            }

            log.info("Successfully wrote search results to BigQuery table: {}.{}", datasetId, tableId);
            
        } catch (Exception e) {
            log.error("Error writing to BigQuery dataset: {}, table: {}", datasetId, tableId, e);
            throw new RuntimeException("Failed to write to BigQuery", e);
        }
    }

    private void createTable(String datasetId, String tableId) {
        Schema schema = Schema.of(
            Field.of("id", StandardSQLTypeName.STRING),
            Field.of("numFound", StandardSQLTypeName.INT64),
            Field.of("success", StandardSQLTypeName.BOOL)
            // Add more fields as needed
        );

        TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        TableInfo tableInfo = TableInfo.newBuilder(TableId.of(datasetId, tableId), tableDefinition)
            .build();

        bigQuery.create(tableInfo);
        log.info("Created BigQuery table: {}.{}", datasetId, tableId);
    }
} 