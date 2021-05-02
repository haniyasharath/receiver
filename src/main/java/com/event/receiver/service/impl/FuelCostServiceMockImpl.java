package com.event.receiver.service.impl;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FuelCostServiceMockImpl {

    private static final Map<String, Double> FUEL_COST_DATA;

    static {
        FUEL_COST_DATA = new HashMap<>();
        FUEL_COST_DATA.put("Bangalore", 89.98);
        FUEL_COST_DATA.put("Delhi", 99.34);
        FUEL_COST_DATA.put("Mumbai", 87.51);
        FUEL_COST_DATA.put("Chenni", 96.72);
        FUEL_COST_DATA.put("Mysure", 93.88);
        FUEL_COST_DATA.put("Mysure", 93.88);
        FUEL_COST_DATA.put("Gujarat", 95.23);
        FUEL_COST_DATA.put("Rajasthan", 98.78);
    }

    public Double getFuelCost(String city) {
        Double fuelCost = 0.0;
        if (FUEL_COST_DATA.containsKey(city)) {
            fuelCost = FUEL_COST_DATA.get(city);
        }
        return fuelCost;
    }
}
