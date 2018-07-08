package ru.airiva.mapping.converter;

import org.springframework.stereotype.Component;
import ru.airiva.dto.ClientDto;
import ru.airiva.entities.TlgClientEntity;

/**
 * @author Ivan
 */
@Component
public class ClientConverter implements Converter<TlgClientEntity, ClientDto>{
    @Override
    public ClientDto convert(TlgClientEntity source) {
        ClientDto client = new ClientDto();
        client.setPhone(source.getPhone());
        client.setUsername(source.getUsername());
        client.setActive(true);
        return client;
    }
}
