package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class ProductSearchResult {
    private boolean success;
    private int numFound;
    private List<RecognizedVehicle> recognizedVehicles;
    private Metadata metadata;
    private SearchEngine searchEngine;
    private List<Facet> facets;
    private List<Product> products;
} 