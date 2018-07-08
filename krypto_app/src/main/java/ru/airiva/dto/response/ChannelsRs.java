package ru.airiva.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.airiva.dto.ChannelDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelsRs extends RsDto {

    private final List<ChannelDto> channels = new ArrayList<>();

    public List<ChannelDto> getChannels() {
        return channels;
    }

}
