package ru.airiva.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.airiva.dto.ProducerDto;

import java.util.List;

/**
 * @author Ivan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranslationDeleteRq extends RqDto {

    private String name;
    private String clientPhone;
    private Long consumer;
    private List<ProducerDto> producers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public Long getConsumer() {
        return consumer;
    }

    public void setConsumer(Long consumer) {
        this.consumer = consumer;
    }

    public List<ProducerDto> getProducers() {
        return producers;
    }

    public void setProducers(List<ProducerDto> producers) {
        this.producers = producers;
    }

    @Override
    public String toString() {
        return "TranslationDeleteRq{" +
                "name='" + name + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                ", consumer=" + consumer +
                ", producers=" + producers +
                '}';
    }
}
