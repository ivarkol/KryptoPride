package ru.airiva.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.airiva.exception.TlgCheckAuthCodeBsException;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.service.cg.TlgInteractionCgService;
import ru.airiva.vo.TlgChannel;

import java.util.List;

@RestController
@RequestMapping("/tlg")
public class TestController {

    private TlgInteractionCgService tlgInteractionCgService;

    @Autowired
    public void setTlgInteractionCgService(TlgInteractionCgService tlgInteractionCgService) {
        this.tlgInteractionCgService = tlgInteractionCgService;
    }

    @GetMapping(value = "/start", params = "phone", produces = "text/html;charset=UTF-8")
    ResponseEntity<String> start(@RequestParam("phone") String phone) {
        String rs;
        try {
            tlgInteractionCgService.start(phone);
            rs = "Start successful";
        } catch (TlgWaitAuthCodeBsException e) {
            rs = e.getMessage();
        } catch (TlgFailAuthBsException e) {
            rs = e.getMessage();
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/auth", params = "phone", produces = "text/html;charset=UTF-8")
    ResponseEntity<String> auth(@RequestParam("phone") String phone) {
        String rs;
        try {
            tlgInteractionCgService.authorize(phone);
            rs = "Authorization successful";
        } catch (TlgWaitAuthCodeBsException e) {
            rs = e.getMessage();
        } catch (TlgFailAuthBsException e) {
            rs = e.getMessage();
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/code", params = {"phone", "code"}, produces = "text/html;charset=UTF-8")
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
        } catch (TlgNeedAuthBsException e) {
            rs = e.getMessage();
        } catch (TlgCheckAuthCodeBsException e) {
            rs = e.getMessage();
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/logout", params = {"phone"}, produces = "text/html;charset=UTF-8")
    ResponseEntity<String> logout(@RequestParam("phone") String phone) {
        tlgInteractionCgService.logout(phone);
        return ResponseEntity.ok("Logout is successful");
    }

    @GetMapping(value = "/chats", params = {"phone"})
    ResponseEntity<List<TlgChannel>> getChats(@RequestParam("phone") String phone) {
        List<TlgChannel> sortedChannels = tlgInteractionCgService.getSortedChannels(phone);
        return ResponseEntity.ok(sortedChannels);
    }

    @GetMapping(value = "/test", produces = "text/html;charset=UTF-8")
    ResponseEntity<String> test() {
        return ResponseEntity.ok("ТЕСТ");
    }
}
