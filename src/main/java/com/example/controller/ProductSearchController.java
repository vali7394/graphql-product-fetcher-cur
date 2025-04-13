package com.example.controller;

import com.example.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductSearchController {
    private final ProductSearchService productSearchService;

    @PostMapping("/search/process")
    public ResponseEntity<String> processKeywords() {
        log.info("Received request to process keywords");
        CompletableFuture<Void> future = productSearchService.processKeywords();
        
        future.exceptionally(throwable -> {
            log.error("Error processing keywords", throwable);
            return null;
        });

        return ResponseEntity.accepted()
            .body("Keyword processing started");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is healthy");
    }
} 