package ru.airiva.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.airiva.dto.ProducerDto;
import ru.airiva.dto.request.TranslationDeleteRq;
import ru.airiva.dto.response.RsDto;
import ru.airiva.dto.response.TranslationsRs;
import ru.airiva.entities.TlgChatEntity;
import ru.airiva.entities.TlgChatPairEntity;
import ru.airiva.entities.TlgClientEntity;
import ru.airiva.entities.TlgTrPackageEntity;
import ru.airiva.mapping.converter.TranslationConverter;
import ru.airiva.service.cg.TlgInteractionCgService;
import ru.airiva.service.fg.PersonFgService;
import ru.airiva.service.fg.TlgChatFgService;
import ru.airiva.service.fg.TlgChatPairFgService;
import ru.airiva.service.fg.TlgClientFgService;
import ru.airiva.service.fg.TlgTrPackageFgService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.airiva.dto.response.RsDto.error;
import static ru.airiva.dto.response.RsDto.success;

/**
 * @author Ivan
 */
@RestController
@RequestMapping("/translations")
public class TranslationsController {

    private static final Logger logger = LoggerFactory.getLogger(TranslationsController.class);

    private TlgTrPackageFgService tlgTrPackageFgService;
    private TranslationConverter translationConverter;
    private TlgInteractionCgService tlgInteractionCgService;
    private PersonFgService personFgService;
    private TlgClientFgService tlgClientFgService;
    private TlgChatFgService tlgChatFgService;
    private TlgChatPairFgService tlgChatPairFgService;

    @Autowired
    public void setTlgTrPackageFgService(TlgTrPackageFgService tlgTrPackageFgService) {
        this.tlgTrPackageFgService = tlgTrPackageFgService;
    }

    @Autowired
    public void setTranslationConverter(TranslationConverter translationConverter) {
        this.translationConverter = translationConverter;
    }

    @Autowired
    public void setTlgInteractionCgService(TlgInteractionCgService tlgInteractionCgService) {
        this.tlgInteractionCgService = tlgInteractionCgService;
    }

    @Autowired
    public void setPersonFgService(PersonFgService personFgService) {
        this.personFgService = personFgService;
    }

    @Autowired
    public void setTlgClientFgService(TlgClientFgService tlgClientFgService) {
        this.tlgClientFgService = tlgClientFgService;
    }

    @Autowired
    public void setTlgChatFgService(TlgChatFgService tlgChatFgService) {
        this.tlgChatFgService = tlgChatFgService;
    }

    @Autowired
    public void setTlgChatPairFgService(TlgChatPairFgService tlgChatPairFgService) {
        this.tlgChatPairFgService = tlgChatPairFgService;
    }

    @GetMapping
    public ResponseEntity<RsDto> translations() {
        TranslationsRs rs;
        try {
            rs = getTranslations();
        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(rs);
    }

    @Transactional
    TranslationsRs getTranslations() {
        TranslationsRs rs = new TranslationsRs();
        logger.debug("GetTranslations started!!!!!!!!!!!!!!!!!!");
        Set<TlgTrPackageEntity> translationsEntity = tlgTrPackageFgService.getTranslations(1L);
        logger.debug("TranslationEntities obtained!!!!!!!!!!!!!!!!!!");
        translationsEntity.forEach(entity -> rs.getTranslationDtos().add(translationConverter.convert(entity)));
        logger.debug("TranslationEntities converted!!!!!!!!!!!!!!!!!!");
        rs.getTranslationDtos().sort((o1, o2) -> {
            if (o1 != null && o2 != null && o1.getName() != null && o2.getName() != null) {
                return o1.getName().compareTo(o2.getName());
            }
            return 0;
        });
        logger.debug("GetTranslations finished!!!!!!!!!!!!!!!!!!");
        return rs;
    }

    @GetMapping("/del")
    public ResponseEntity<RsDto> deleteTranslation(@RequestParam("id") String id) {
        try {
            tlgTrPackageFgService.deleteTranslationById(Long.valueOf(id));
        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(success());
    }

    @PostMapping("/save")
    public ResponseEntity<RsDto> saveTranslation(@RequestBody TranslationDeleteRq rq) {

        logger.info(rq.toString());

        try {

            saveTlgTrPackageEntity(rq);
            String clientPhone = rq.getClientPhone();
            Long target = rq.getConsumer();
            List<ProducerDto> producers = rq.getProducers();
            for (ProducerDto pd : producers) {
                tlgInteractionCgService.includeParsing(clientPhone, pd.getChannelId(), target);
            }


        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(success());
    }

    @Transactional
    void saveTlgTrPackageEntity(TranslationDeleteRq rq) {
        String clientPhone = rq.getClientPhone();
        Long target = rq.getConsumer();
        List<ProducerDto> producers = rq.getProducers();

        TlgTrPackageEntity tlgTrPackageEntity = new TlgTrPackageEntity();
        tlgTrPackageEntity.setEnabled(true);
        tlgTrPackageEntity.setName(rq.getName());
        tlgTrPackageEntity.setPersonEntity(personFgService.getById(1L));
        tlgTrPackageEntity = tlgTrPackageFgService.saveTranslation(tlgTrPackageEntity);
        Set<TlgChatPairEntity> tlgChatPairEntitySet = new HashSet<>(producers.size());

        final TlgClientEntity client = tlgClientFgService.getByPhone(clientPhone);

        TlgChatEntity destChat = tlgChatFgService.getById(target);
        if (destChat == null) {
            destChat = new TlgChatEntity();
            destChat.setTlgChatId(target);
            destChat.setChannel(true);
            destChat = tlgChatFgService.saveChat(destChat);
        }


        for (ProducerDto pd : producers) {
            TlgChatEntity srcChat = tlgChatFgService.getById(pd.getChannelId());
            if (srcChat == null) {
                srcChat = new TlgChatEntity();
                srcChat.setChannel(true);
                srcChat.setTlgChatId(pd.getChannelId());
                srcChat = tlgChatFgService.saveChat(srcChat);
            }

            TlgChatPairEntity pair = new TlgChatPairEntity();
            pair.setDestChat(destChat);
            pair.setSrcChat(srcChat);
            pair.setDelay(pd.getDelay());
            pair.setOrderedExpressionEntities(new HashSet<>());
            pair.setTlgClientEntity(client);
            pair.setTlgTrPackageEntity(tlgTrPackageEntity);
            tlgChatPairEntitySet.add(tlgChatPairFgService.savePair(pair));
        }
        tlgTrPackageEntity.setTlgChatPairEntities(tlgChatPairEntitySet);

        tlgTrPackageFgService.saveTranslation(tlgTrPackageEntity);
    }
}
