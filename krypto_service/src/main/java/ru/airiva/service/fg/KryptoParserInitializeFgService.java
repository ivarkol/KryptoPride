package ru.airiva.service.fg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.entities.TlgChatPairEntity;
import ru.airiva.service.da.repository.TlgChatPairRepo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan
 */
@Service
public class KryptoParserInitializeFgService {

    private TlgChatPairRepo tlgChatPairRepo;

    @Autowired
    public void setTlgChatPairRepo(TlgChatPairRepo tlgChatPairRepo) {
        this.tlgChatPairRepo = tlgChatPairRepo;
    }

    public Set<TlgChatPairEntity> obtainEnabledTlgChatPairs(final String phone) {
        Set<TlgChatPairEntity> pairs = new HashSet<>();
        if (phone != null) {
            pairs.addAll(tlgChatPairRepo.getTlgChatPairEntitiesByTlgClientEntity_PhoneAndTlgTrPackageEntity_Enabled(phone, true));
        }
        return pairs;
    }

    public TlgChatPairEntity obtainEnabledTlgChatPair(final String phone, final long source, final long target) {

        TlgChatPairEntity pair = null;
        if (phone != null) {
            pair = tlgChatPairRepo.getTlgChatPairEntityBySrcChat_TlgChatIdAndDestChat_TlgChatIdAndTlgClientEntity_PhoneAndTlgTrPackageEntity_Enabled(source, target, phone, true);
        }

//        pair = new TlgChatPairEntity();
//        TlgChatEntity srcChat = new TlgChatEntity();
//        srcChat.setChannel(true);
//        srcChat.setTlgChatId(source);
//        pair.setSrcChat(srcChat);
//
//        TlgChatEntity destChat = new TlgChatEntity();
//        destChat.setChannel(true);
//        destChat.setTlgChatId(target);
//        pair.setDestChat(destChat);

        return pair;
    }

}
