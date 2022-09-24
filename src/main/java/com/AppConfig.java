package com;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@ComponentScan("com")
public class AppConfig {
    private final static LocalDate[] date = new LocalDate[1];

    @Bean(name = "date")
    public static LocalDate[] virtualDate() {
        return date;
    }
}
