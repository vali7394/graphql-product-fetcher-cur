package com.gpc.catalog.model;

import lombok.Data;
import java.util.List;

@Data
public class QueryClassification {
    private List<String> recognizedAttributes;
} 