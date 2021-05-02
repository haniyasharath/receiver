# Receiving Service
 Receiver responsible for processing incoming events

## Components
* Spring Boot 2.5.0-RC1
* Spring Cloud Stream
* RabbitMQ message broker
* HazalCast Caching
* Gradle

## General Design
* Listen to the incoming messages from RabbitMQ 
* Looks for fuel price for a random city via Mocked service
* Caches fetched fuel prices for 24 hours
* HazalCast Cached price will be cleared every day midnight (12:00 AM) 

# Build
```gradle clean build```

# Running Locally

```gradle :bootRun```

# TODO
* Replace Mocked Fuel Cost service with Third Party provider 
* JUNIT Test cases
