package com.microservice.stock.uploadexcel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UploadexcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadexcelApplication.class, args);
    }

}
