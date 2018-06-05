package ru.airiva.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.airiva.client.TlgInteractionService;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;

import java.util.List;

@RestController
@RequestMapping("/tlg")
public class TestController {

    private TlgInteractionService tlgInteractionService;

    @Autowired
    public void setTlgInteractionService(TlgInteractionService tlgInteractionService) {
        this.tlgInteractionService = tlgInteractionService;
    }

    @GetMapping(value = "/start", params = "phone")
    ResponseEntity<String> start(@RequestParam("phone") String phone) {
        String rs;
        try {
            tlgInteractionService.start(phone);
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

    @GetMapping(value = "/auth", params = "phone")
    ResponseEntity<String> auth(@RequestParam("phone") String phone) {
        String rs;
        try {
            tlgInteractionService.authorize(phone);
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

    @GetMapping("/code/{code}")
    ResponseEntity<String> checkCode(@PathVariable("code") String code) {
        String rs;
        try {
            tlgInteractionService.checkCode(code);
            rs = "Authentication successful";
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping("/logout")
    ResponseEntity<String> logout() {
        tlgInteractionService.logout();
        return ResponseEntity.ok("Logout is successful");
    }

    @GetMapping("/chats")
    ResponseEntity<List<String>> getChats() {
        List<String> chats = tlgInteractionService.getChats();
        return ResponseEntity.ok(chats);
    }
}
