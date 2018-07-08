package ru.airiva.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.airiva.dto.request.ClientRq;
import ru.airiva.dto.response.ChannelsRs;
import ru.airiva.dto.response.ClientsRs;
import ru.airiva.dto.response.RsDto;
import ru.airiva.entities.TlgClientEntity;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.mapping.converter.ChannelConverter;
import ru.airiva.mapping.converter.ClientConverter;
import ru.airiva.service.cg.TlgInteractionCgService;
import ru.airiva.service.fg.PersonFgService;
import ru.airiva.service.fg.TlgClientFgService;
import ru.airiva.vo.TlgChannel;

import java.util.List;
import java.util.Set;

import static ru.airiva.dto.response.RsDto.*;

/**
 * @author Ivan
 */
@RestController
@RequestMapping("/clients")
public class ClientsController {

    private PersonFgService personFgService;
    private TlgClientFgService tlgClientFgService;
    private TlgInteractionCgService tlgInteractionCgService;

    private ClientConverter clientConverter;
    private ChannelConverter channelConverter;

    @Autowired
    public void setPersonFgService(PersonFgService personFgService) {
        this.personFgService = personFgService;
    }

    @Autowired
    public void setTlgClientFgService(TlgClientFgService tlgClientFgService) {
        this.tlgClientFgService = tlgClientFgService;
    }

    @Autowired
    public void setTlgInteractionCgService(TlgInteractionCgService tlgInteractionCgService) {
        this.tlgInteractionCgService = tlgInteractionCgService;
    }

    @Autowired
    public void setClientConverter(ClientConverter clientConverter) {
        this.clientConverter = clientConverter;
    }

    @Autowired
    public void setChannelConverter(ChannelConverter channelConverter) {
        this.channelConverter = channelConverter;
    }

    @GetMapping
    public ResponseEntity<RsDto> clients() {
        ClientsRs rs;
        try {
            rs = new ClientsRs();
            Set<TlgClientEntity> tlgClientEntities = personFgService.clientsByPersonId(1L);
            if (CollectionUtils.isNotEmpty(tlgClientEntities)) {
                tlgClientEntities.forEach(tlgClientEntity -> rs.getClients().add(clientConverter.convert(tlgClientEntity)));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping("/consumers")
    public ResponseEntity<RsDto> consumers(@RequestParam("phone") String phone) {
        ChannelsRs rs;
        try {
            rs = new ChannelsRs();
            List<TlgChannel> sortedChannels = tlgInteractionCgService.getSortedChannels(phone);
            if (CollectionUtils.isNotEmpty(sortedChannels)) {
                sortedChannels.forEach(sortedChannel -> {
                    if (sortedChannel != null) rs.getChannels().add(channelConverter.convert(sortedChannel));
                });
            }
        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/start", params = "phone")
    ResponseEntity<RsDto> start(@RequestParam("phone") String phone) {
        try {
            tlgInteractionCgService.startParsing(phone);
        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(success());
    }

    @GetMapping(value = "/stop", params = "phone")
    ResponseEntity<RsDto> stop(@RequestParam("phone") String phone) {
        try {
            tlgInteractionCgService.stopParsing(phone);
        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(success());
    }

    @GetMapping(value = "/auth", params = "phone")
    ResponseEntity<RsDto> auth(@RequestParam("phone") String phone) {
        RsDto rs;
        try {
            tlgInteractionCgService.authorize(phone);
            rs = success();
        } catch (TlgWaitAuthCodeBsException e) {
            rs = codewait();
        } catch (Exception e) {
            rs = error(e);
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/code", params = {"phone", "code"})
    ResponseEntity<RsDto> checkCode(@RequestParam("phone") String phone,
                                    @RequestParam("code") String code) {
        try {
            boolean isCodeCorrect = tlgInteractionCgService.checkCode(phone, code);
            if (!isCodeCorrect) {
                throw new Exception();
            }
        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }
        return ResponseEntity.ok(success());
    }

    @GetMapping(value = "/delete", params = {"phone"})
    ResponseEntity<RsDto> delete(@RequestParam("phone") String phone) {

        RsDto rs;
        try {
            tlgInteractionCgService.logout(phone);
            tlgClientFgService.deleteByPhone(phone);
            rs = success();
        } catch (Exception e) {
            rs = error(e);
        }
        return ResponseEntity.ok(rs);
    }

    @PostMapping(value = "/add")
    ResponseEntity<RsDto> add(@RequestBody ClientRq rq) {

        RsDto rs;
        try {
            TlgClientEntity clientEntity = new TlgClientEntity();
            clientEntity.setPhone(rq.getPhone());
            clientEntity.setUsername(rq.getUsername());
            tlgClientFgService.addClient(clientEntity);
            rs = success();
        } catch (Exception e){
            rs = error(e);
        }
        return ResponseEntity.ok(rs);
    }


}
