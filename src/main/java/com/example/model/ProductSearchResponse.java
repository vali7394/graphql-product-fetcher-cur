package com.example.model;

import lombok.Data;

@Data
public class ProductSearchResponse {
    private ProductSearchResult products;
}

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

@Data
class RecognizedVehicle {
    private Make make;
    private Model model;
    private String remainingPhrase;
    private VehicleType vehicleType;
    private int year;
}

@Data
class Make {
    private String id;
    private String value;
}

@Data
class Model {
    private String id;
    private String value;
}

@Data
class VehicleType {
    private String id;
    private String value;
}

@Data
class Metadata {
    private ApplicationSpecific applicationSpecific;
}

@Data
class ApplicationSpecific {
    private QueryClassification queryClassification;
}

@Data
class QueryClassification {
    private List<String> recognizedAttributes;
}

@Data
class SearchEngine {
    private String searchQuery;
    private String entity;
    private List<String> matchedCategories;
}

@Data
class Facet {
    private String name;
    private String displayName;
    private String filterPlace;
    private boolean open;
    private List<FacetValue> values;
}

@Data
class FacetValue {
    private String value;
    private int count;
}

@Data
class Product {
    private String id;
    private String applicationTitle;
    private ProductAttributes productAttributes;
    private String brand;
    private String country;
    private String status;
    private String mainNumber;
    private String oem;
    private List<String> products;
    private String storeTitle;
    private String lineObservation;
    private String time;
    private int views;
    private PartType partType;
}

@Data
class ProductAttributes {
    private List<SpecialAttribute> specialAttributes;
}

@Data
class SpecialAttribute {
    private String value;
    private String attributeName;
}

@Data
class PartType {
    private String name;
} 