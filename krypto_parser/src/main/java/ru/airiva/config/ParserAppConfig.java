package ru.airiva.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@ComponentScan(value = {"ru.airiva.client", "ru.airiva.service", "ru.airiva.properties", "ru.airiva.utils"})
@PropertySource(value = "classpath:krypto_parser_timeouts.properties", encoding = "UTF-8")
public class ParserAppConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
