package com.testai.endpointservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EndpointServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EndpointServiceApplication.class, args);
    }
}