package com.example.service;

import com.example.config.AppProperties;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsService {
    private final AppProperties appProperties;
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    public List<String> readKeywords() {
        List<String> keywords = new ArrayList<>();
        String bucketName = appProperties.getGcs().getBucket();
        String fileName = appProperties.getGcs().getFile();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(storage.get(bucketName, fileName).getContent()),
                        StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    keywords.add(line.trim());
                }
            }
            
            log.info("Successfully read {} keywords from GCS bucket: {}, file: {}", 
                    keywords.size(), bucketName, fileName);
            
        } catch (Exception e) {
            log.error("Error reading keywords from GCS bucket: {}, file: {}", 
                    bucketName, fileName, e);
            throw new RuntimeException("Failed to read keywords from GCS", e);
        }

        return keywords;
    }
} 