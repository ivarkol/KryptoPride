package ru.airiva.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Ivan
 */
@Configuration
@ComponentScan(value = {"ru.airiva.utils"})
@PropertySource(value = "classpath:krypto_parser_timeouts.properties", encoding = "UTF-8")
public class ServiceAppConfig {
}
