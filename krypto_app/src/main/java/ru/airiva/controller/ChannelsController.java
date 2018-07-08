package ru.airiva.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.airiva.dto.response.ChannelsRs;
import ru.airiva.dto.response.RsDto;

import static ru.airiva.dto.response.RsDto.error;

/**
 * @author Ivan
 */
@RestController
@RequestMapping("/channels")
public class ChannelsController {

    @GetMapping("/")
    public ResponseEntity<RsDto> consumers(@RequestParam("phone") String phone,
                                           @RequestParam("id") Long clientId) {
        ChannelsRs rs = null;
        try {


        } catch (Exception e) {
            return ResponseEntity.ok(error(e));
        }

        return ResponseEntity.ok(rs);
    }

}
