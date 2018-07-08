package ru.airiva.service.fg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.entities.TlgChatPairEntity;
import ru.airiva.service.da.repository.TlgChatPairRepo;

import java.util.Set;

/**
 * @author Ivan
 */
@Service
public class TlgChatPairFgService {

    private TlgChatPairRepo tlgChatPairRepo;

    @Autowired
    public void setTlgChatPairRepo(TlgChatPairRepo tlgChatPairRepo) {
        this.tlgChatPairRepo = tlgChatPairRepo;
    }

    public Set<TlgChatPairEntity> getChatPairsByTrPackage(Long trPackageId) {
        return tlgChatPairRepo.findByTlgTrPackageEntityId(trPackageId);
    }
}
