package ru.airiva.mapping.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.airiva.dto.TranslationDto;
import ru.airiva.entities.TlgChatPairEntity;
import ru.airiva.entities.TlgTrPackageEntity;
import ru.airiva.service.fg.TlgChatPairFgService;

import java.util.Set;

/**
 * @author Ivan
 */
@Component
public class TranslationConverter implements Converter<TlgTrPackageEntity, TranslationDto> {

    private TlgChatPairFgService tlgChatPairFgService;

    @Autowired
    public void setTlgChatPairFgService(TlgChatPairFgService tlgChatPairFgService) {
        this.tlgChatPairFgService = tlgChatPairFgService;
    }

    @Override
    public TranslationDto convert(TlgTrPackageEntity source) {
        if (source == null) return null;
        TranslationDto translationDto = new TranslationDto();
        translationDto.setId(source.getId().toString());
        translationDto.setName(source.getName());

        Set<TlgChatPairEntity> chatPairs = tlgChatPairFgService.getChatPairsByTrPackage(source.getId());
        if (CollectionUtils.isNotEmpty(chatPairs)) {
            source.getTlgChatPairEntities().stream().findFirst().ifPresent(tlgChatPairEntity -> {
                        translationDto.setAccount(tlgChatPairEntity.getTlgClientEntity().getPhone());
                        translationDto.setConsumer(tlgChatPairEntity.getDestChat().getTlgChatId().toString());
                    });
        }
        translationDto.setActive(source.isEnabled());
        return translationDto;
    }
}
