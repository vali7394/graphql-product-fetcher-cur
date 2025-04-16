package com.example.controller;

import com.example.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductSearchController {
    private final ProductSearchService productSearchService;

    @PostMapping("/search/process")
    public ResponseEntity<String> processKeywords() {
        log.info("Received request to process keywords");
        try {
            productSearchService.processKeywords();
            return ResponseEntity.ok("Keywords processed successfully");
        } catch (Exception e) {
            log.error("Error processing keywords", e);
            return ResponseEntity.internalServerError()
                .body("Error processing keywords: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is healthy");
    }
} 