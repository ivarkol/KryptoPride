package ru.airiva.service.fg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TlgChatPairFgService.class);

    private TlgChatPairRepo tlgChatPairRepo;

    @Autowired
    public void setTlgChatPairRepo(TlgChatPairRepo tlgChatPairRepo) {
        this.tlgChatPairRepo = tlgChatPairRepo;
    }

    public Set<TlgChatPairEntity> getChatPairsByTrPackage(Long trPackageId) {
        return tlgChatPairRepo.findByTlgTrPackageEntityId(trPackageId);
    }

    public TlgChatPairEntity savePair(TlgChatPairEntity pair) {
        TlgChatPairEntity tlgChatPairEntity = tlgChatPairRepo.save(pair);
        logger.info("Pair saved: {}", pair);
        return tlgChatPairEntity;
    }
}
