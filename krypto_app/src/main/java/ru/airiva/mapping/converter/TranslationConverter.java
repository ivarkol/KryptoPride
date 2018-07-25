package ru.airiva.mapping.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.airiva.dto.TranslationDto;
import ru.airiva.entities.TlgChatPairEntity;
import ru.airiva.entities.TlgTrPackageEntity;
import ru.airiva.exception.TlgDefaultBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgTimeoutBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.service.cg.TlgInteractionCgService;
import ru.airiva.service.fg.TlgChatPairFgService;
import ru.airiva.vo.TlgChannel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Ivan
 */
@Component
public class TranslationConverter implements Converter<TlgTrPackageEntity, TranslationDto> {

    private TlgChatPairFgService tlgChatPairFgService;
    private TlgInteractionCgService tlgInteractionCgService;

    @Autowired
    public void setTlgChatPairFgService(TlgChatPairFgService tlgChatPairFgService) {
        this.tlgChatPairFgService = tlgChatPairFgService;
    }

    @Autowired
    public void setTlgInteractionCgService(TlgInteractionCgService tlgInteractionCgService) {
        this.tlgInteractionCgService = tlgInteractionCgService;
    }

    @Override
    public TranslationDto convert(TlgTrPackageEntity source) {
        if (source == null) return null;
        TranslationDto translationDto = new TranslationDto();
        translationDto.setId(source.getId().toString());
        translationDto.setName(source.getName());

        Set<TlgChatPairEntity> chatPairs = tlgChatPairFgService.getChatPairsByTrPackage(source.getId());
        if (CollectionUtils.isNotEmpty(chatPairs)) {
            chatPairs.stream().findFirst().ifPresent(tlgChatPairEntity -> {
                translationDto.setAccount(tlgChatPairEntity.getTlgClientEntity().getPhone());
                translationDto.setConsumer(chatName(tlgChatPairEntity.getTlgClientEntity().getPhone(), tlgChatPairEntity.getDestChat().getTlgChatId()));
            });
        }
        translationDto.setActive(source.isEnabled());
        return translationDto;
    }

    private String chatName(String phone, long chatId) {
        String chatName = null;
        List<TlgChannel> sortedChannels = null;
        try {
            sortedChannels = tlgInteractionCgService.getSortedChannels(phone);
        } catch (TlgWaitAuthCodeBsException | TlgNeedAuthBsException | TlgDefaultBsException | TlgTimeoutBsException e) {
            e.printStackTrace();
        }
        Optional<TlgChannel> first = sortedChannels.stream().filter(tlgChannel -> chatId == tlgChannel.getChatId()).findFirst();
        if (first.isPresent()) {
            chatName = first.get().getTitle();
        }
        return chatName;
    }
}
