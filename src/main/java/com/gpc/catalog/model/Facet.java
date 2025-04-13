package com.gpc.catalog.model;

import lombok.Data;
import java.util.List;

@Data
public class Facet {
    private String name;
    private List<FacetValue> values;
} 