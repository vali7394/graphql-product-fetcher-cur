package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class Product {
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