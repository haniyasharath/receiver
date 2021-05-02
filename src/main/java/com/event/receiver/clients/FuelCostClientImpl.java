package com.event.receiver.clients;

import com.event.receiver.service.impl.FuelCostServiceMockImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FuelCostClientImpl implements FuelCostClient {

    @Autowired
    private FuelCostServiceMockImpl fuelCostService;

    @Override
    public Double getFuelCost(String city) {
        return fuelCostService.getFuelCost(city);
    }
}
