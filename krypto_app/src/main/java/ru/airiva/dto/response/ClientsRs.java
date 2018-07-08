package ru.airiva.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.airiva.dto.ClientDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientsRs extends RsDto {

    private final List<ClientDto> clients = new ArrayList<>();

    public List<ClientDto> getClients() {
        return clients;
    }

}
