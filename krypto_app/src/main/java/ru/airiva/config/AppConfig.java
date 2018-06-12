package ru.airiva.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(value = {"ru.airiva.controller"})
@Import({ParserAppConfig.class, ServiceAppConfig.class})
@EnableWebMvc
public class AppConfig {
}
