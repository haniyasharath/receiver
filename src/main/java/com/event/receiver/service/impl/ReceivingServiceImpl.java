package com.event.receiver.service.impl;

import com.event.receiver.clients.FuelCostClient;
import com.event.receiver.dto.EventInputDto;
import com.event.receiver.dto.FuellidStatus;
import com.event.receiver.service.ReceivingService;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class ReceivingServiceImpl implements ReceivingService {

    Logger log = LoggerFactory.getLogger(ReceivingServiceImpl.class);

    public static final String HAZEL_CAST_FUEL_COST_DETAILS = "hazelCastFuelCostDetails";

    public static final String HAZEL_CAST_FUELLID_STATUS = "hazelCastFuellidStatus";

    @Autowired
    private FuelCostClient fuelCostClient;

    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance fuelCostCacheStore;

    @StreamListener(Processor.INPUT)
    public void handleEvent(EventInputDto inputDto) {
        if (Objects.isNull(inputDto) || !StringUtils.hasText(inputDto.getCity())) {
            log.info("Receiving empty Input - {}", inputDto);
            return;
        }
        final String city = inputDto.getCity();
        if (inputDto.isFuellid()) {
            if (!fuelCostCacheStore.getMap(HAZEL_CAST_FUEL_COST_DETAILS).containsKey(city)) {
                Double fuelCost = fuelCostClient.getFuelCost(inputDto.getCity());
                if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(fuelCost)) == 0) {
                    log.info("Fuel cost details is not available for the given city - {}", inputDto.getCity());
                    return;
                }
                fuelCostCacheStore.getMap(HAZEL_CAST_FUEL_COST_DETAILS).put(city, BigDecimal.valueOf(fuelCost), -1, TimeUnit.SECONDS);
                log.info("Fuel cost details has been updated for the given city - {}", inputDto.getCity());
            }
            FuellidStatus fuellidStatus = new FuellidStatus(true, LocalTime.now());
            fuelCostCacheStore.getMap(HAZEL_CAST_FUELLID_STATUS).put(city, fuellidStatus, 0, TimeUnit.SECONDS);
        } else {
            Object pFuellidStatus = fuelCostCacheStore.getMap(HAZEL_CAST_FUELLID_STATUS).get(city);
            FuellidStatus prevFuellidStatus = (FuellidStatus) pFuellidStatus;
            if (prevFuellidStatus.isOpen() && fuelCostCacheStore.getMap(HAZEL_CAST_FUEL_COST_DETAILS).containsKey(city)) {
                Object fCost = fuelCostCacheStore.getMap(HAZEL_CAST_FUEL_COST_DETAILS).get(city);
                BigDecimal fuelCost = (BigDecimal) fCost;
                calculateFuelCost(prevFuellidStatus, fuelCost);
            } else if (!prevFuellidStatus.isOpen()) {
                log.info("Fuellid never opened before for the provided city- {}", inputDto.getCity());
            } else {
                log.info("Fuel cost details is not available for the given city - {}", inputDto.getCity());
            }
        }
        log.info("Processed Event - {}", inputDto);
    }

    private void calculateFuelCost(FuellidStatus prevFuellidStatus, BigDecimal fuelCost) {
        LocalTime currentTime = LocalTime.now();
        Duration lidOpenCloseDuration = Duration.between(prevFuellidStatus.getLocalTime(), currentTime);
        long lidOpenTime = lidOpenCloseDuration.getSeconds();
        double totalFuelIntake = BigDecimal.valueOf(lidOpenTime).divide(BigDecimal.valueOf(30), 2, RoundingMode.DOWN).doubleValue();
        BigDecimal totalFuelCost = fuelCost.multiply(BigDecimal.valueOf(totalFuelIntake), new MathContext(0, RoundingMode.DOWN));
        log.info("Fuellid Open Time = {} secs", lidOpenTime);
        log.info("No. of liter Fuel filled = {} ltr", totalFuelIntake);
        log.info("Calculated Cost of Fuel = Rs. {}", totalFuelCost);
    }

    @Scheduled(cron = "${hazelcast.clear}")
    public void clearFuelCostData() {
        if (fuelCostCacheStore.getMap(HAZEL_CAST_FUEL_COST_DETAILS).size() > 0) {
            fuelCostCacheStore.getMap(HAZEL_CAST_FUEL_COST_DETAILS).clear();
        }
    }

    /**
     * Testing Receiving service code without RabbitMQ installed
     */
    @Scheduled(cron = "${event.trigger}")
    public void triggerEvent() {
        handleEvent(new EventInputDto(true, "Bangalore"));
        try {
            Thread.sleep(35000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handleEvent(new EventInputDto(false, "Bangalore"));
    }
}
