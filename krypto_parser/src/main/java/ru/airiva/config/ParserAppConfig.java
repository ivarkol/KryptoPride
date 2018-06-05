package ru.airiva.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"ru.airiva.client", "ru.airiva.service"})
public class ParserAppConfig {
}
