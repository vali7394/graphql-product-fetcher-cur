package com.gpc.catalog.model;

import lombok.Data;

@Data
public class RecognizedVehicle {
    private Make make;
    private Model model;
    private String remainingPhrase;
    private VehicleType vehicleType;
    private int year;
} 