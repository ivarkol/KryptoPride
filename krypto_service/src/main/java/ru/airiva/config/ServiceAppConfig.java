package ru.airiva.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ivan
 */
@Configuration
@ComponentScan(value = {"ru.airiva.utils", "ru.airiva.properties", "ru.airiva.service"})
//@Import({ServicePersistConfig.class})
public class ServiceAppConfig {
}
