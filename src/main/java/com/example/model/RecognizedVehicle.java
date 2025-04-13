package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class RecognizedVehicle {
    private Make make;
    private Model model;
    private String remainingPhrase;
    private VehicleType vehicleType;
    private int year;
} 