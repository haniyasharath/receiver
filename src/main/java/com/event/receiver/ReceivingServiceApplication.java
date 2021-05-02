package com.event.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBinding(Processor.class)
@EnableScheduling
public class ReceivingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceivingServiceApplication.class, args);
    }
}