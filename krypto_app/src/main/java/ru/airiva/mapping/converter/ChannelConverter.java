package ru.airiva.mapping.converter;

import org.springframework.stereotype.Component;
import ru.airiva.dto.ChannelDto;
import ru.airiva.vo.TlgChannel;

/**
 * @author Ivan
 */
@Component
public class ChannelConverter implements Converter<TlgChannel, ChannelDto> {
    @Override
    public ChannelDto convert(TlgChannel source) {
        if (source == null) return null;
        ChannelDto channel = new ChannelDto();
        channel.setId(String.valueOf(source.getChatId()));
        channel.setUsername(source.getTitle());
        return channel;
    }
}
