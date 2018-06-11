package ru.airiva.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
            tlgInteractionCgService.startParsing(phone);
            rs = "Start parsing successful";
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/stop", params = "phone", produces = "text/html;charset=UTF-8")
    ResponseEntity<String> stop(@RequestParam("phone") String phone) {
        String rs;
        try {
            tlgInteractionCgService.stopParsing(phone);
            rs = "Stop parsing successful";
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
    ResponseEntity<Response> getChats(@RequestParam("phone") String phone) {
        Response rs;
        try {
            List<TlgChannel> sortedChannels = tlgInteractionCgService.getSortedChannels(phone);
            rs = new Response(sortedChannels);
        } catch (Exception e) {
            rs = new Response(e.getMessage());
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/incparse")
    ResponseEntity<String> includeParsing(@RequestParam("phone") String phone,
                                          @RequestParam("source") long source,
                                          @RequestParam("target") long target,
                                          @RequestParam("delay") long delay) {
        String rs;
        try {
            tlgInteractionCgService.includeParsing(phone, source, target, delay);
            rs = "Parsing included successfully";
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/exparse")
    ResponseEntity<String> excludeParsing(@RequestParam("phone") String phone,
                                          @RequestParam("source") long source,
                                          @RequestParam("target") long target) {
        String rs;
        try {
            tlgInteractionCgService.excludeParsing(phone, source, target);
            rs = "Parsing excluded successfully";
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/addexpr")
    ResponseEntity<String> addExpression(@RequestParam("phone") String phone,
                                         @RequestParam("source") long source,
                                         @RequestParam("target") long target,
                                         @RequestParam("search") String search,
                                         @RequestParam("replacement") String replacement,
                                         @RequestParam("order") int order) {
        String rs;
        try {
            tlgInteractionCgService.addParsingExpression(phone, source, target, search, replacement, order);
            rs = "Expression added successfully";
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/delexpr")
    ResponseEntity<String> removeExpression(@RequestParam("phone") String phone,
                                            @RequestParam("source") long source,
                                            @RequestParam("target") long target,
                                            @RequestParam("search") String search,
                                            @RequestParam("replacement") String replacement) {
        String rs;
        try {
            tlgInteractionCgService.removeParsingExpression(phone, source, target, search, replacement);
            rs = "Expression removed successfully";
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping(value = "/delay")
    ResponseEntity<String> changeDelay(@RequestParam("phone") String phone,
                                            @RequestParam("source") long source,
                                            @RequestParam("target") long target,
                                            @RequestParam("delay") long delay) {
        String rs;
        try {
            tlgInteractionCgService.setMessageSendingDelay(phone, source, target, delay);
            rs = "Message sending delay changed successfully";
        } catch (Exception e) {
            rs = e.getMessage();
        }
        return ResponseEntity.ok(rs);
    }


    @GetMapping(value = "/test", produces = "text/html;charset=UTF-8")
    ResponseEntity<String> test() {
        return ResponseEntity.ok("ТЕСТ");
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Response {
        private List<TlgChannel> channels;
        private String error;

        public List<TlgChannel> getChannels() {
            return channels;
        }

        public void setChannels(List<TlgChannel> channels) {
            this.channels = channels;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Response(List<TlgChannel> channels) {
            this.channels = channels;
        }

        public Response(String error) {
            this.error = error;
        }
    }

}
