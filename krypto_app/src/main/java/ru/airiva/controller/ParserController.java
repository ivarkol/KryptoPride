package ru.airiva.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import ru.airiva.dto.request.ParserRq;
import ru.airiva.dto.response.RsDto;
import ru.airiva.service.cg.TlgInteractionCgService;

import static ru.airiva.dto.response.RsDto.error;
import static ru.airiva.dto.response.RsDto.success;

/**
 * @author Ivan
 */
@RestController
@RequestMapping("/parser")
public class ParserController {

    private TlgInteractionCgService tlgInteractionCgService;

    @Autowired
    public void setTlgInteractionCgService(TlgInteractionCgService tlgInteractionCgService) {
        this.tlgInteractionCgService = tlgInteractionCgService;
    }

    @PostMapping(value = "/start")
    ResponseEntity<RsDto> start(@RequestBody ParserRq rq) {
        RsDto rs;
        try {
            Assert.notNull(rq.getPhone(), "Phone is null!!!");
            tlgInteractionCgService.startParsing(rq.getPhone());
            rs = success();
        } catch (Exception e) {
            rs = error(e);
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/stop", params = "phone")
    ResponseEntity<RsDto> stop(@RequestParam("phone") String phone) {
        RsDto rs;
        try {
            tlgInteractionCgService.stopParsing(phone);
            rs = success();
        } catch (Exception e) {
            rs = error(e);
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/auth", params = "phone")
    ResponseEntity<RsDto> auth(@RequestParam("phone") String phone) {
        RsDto rs;
        try {
            tlgInteractionCgService.authorize(phone);
            rs = success();
        } catch (Exception e) {
            rs = error(e);
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/code", params = {"phone", "code"})
    ResponseEntity<String> checkCode(@RequestParam("phone") String phone,
                                     @RequestParam("code") String code) {
        String rs;
        try {
            boolean isCodeCorrect = tlgInteractionCgService.checkCode(phone, code);
            if (isCodeCorrect) {
                rs = "Authentication successful";
            } else {
                rs = "Code is incorrect";
            }
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }


}
