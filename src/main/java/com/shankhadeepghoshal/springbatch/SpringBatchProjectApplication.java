package com.shankhadeepghoshal.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchProjectApplication.class, args);
    }
}
