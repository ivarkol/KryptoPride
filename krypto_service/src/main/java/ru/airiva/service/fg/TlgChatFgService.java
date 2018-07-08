package ru.airiva.service.fg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.entities.TlgChatEntity;
import ru.airiva.service.da.repository.TlgChatRepo;

/**
 * @author Ivan
 */
@Service
public class TlgChatFgService {

    private TlgChatRepo tlgChatRepo;

    @Autowired
    public void setTlgChatRepo(TlgChatRepo tlgChatRepo) {
        this.tlgChatRepo = tlgChatRepo;
    }

    public TlgChatEntity getById(Long chatId) {
        return tlgChatRepo.findById(chatId).orElse(null);
    }

    public TlgChatEntity saveChat(TlgChatEntity tlgChatEntity) {
        return tlgChatRepo.saveAndFlush(tlgChatEntity);
    }
}
