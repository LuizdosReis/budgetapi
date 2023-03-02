package com.budgetapi;

import com.budgetapi.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class BudgetapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetapiApplication.class, args);
    }
}
