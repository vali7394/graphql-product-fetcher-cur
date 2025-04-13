package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class Facet {
    private String name;
    private String displayName;
    private String filterPlace;
    private boolean open;
    private List<FacetValue> values;
} 