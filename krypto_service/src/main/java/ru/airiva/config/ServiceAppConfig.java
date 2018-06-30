package ru.airiva.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Ivan
 */
@Configuration
@ComponentScan(value = {"ru.airiva"}, excludeFilters = @Filter(Configuration.class))
@Import({ServicePersistConfig.class})
public class ServiceAppConfig {
}
