package ru.airiva.service.fg;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.airiva.entities.TlgChatEntity;
import ru.airiva.entities.TlgClientEntity;
import ru.airiva.service.da.repository.TlgClientRepo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ivan
 */
@Service
public class TlgClientFgService {

    private TlgClientRepo tlgClientRepo;

    @Autowired
    public void setTlgClientRepo(TlgClientRepo tlgClientRepo) {
        this.tlgClientRepo = tlgClientRepo;
    }

    @Transactional
    public Set<TlgChatEntity> getOwnChannels(String phone) {
        Set<TlgChatEntity> ownChannels = new HashSet<>();
        TlgClientEntity client = tlgClientRepo.findByPhone(phone);
        if (client != null && CollectionUtils.isNotEmpty(client.getOwnChats())) {
            client.getOwnChats().stream().filter(TlgChatEntity::isChannel).collect(Collectors.toSet()).forEach(channel ->
            {
                if (channel != null) ownChannels.add(channel);
            });
        }
        return ownChannels;
    }

    public TlgClientEntity getByPhone(String phone) {
        return tlgClientRepo.findByPhone(phone);
    }

    public void deleteByPhone(String phone) {
        tlgClientRepo.deleteByPhone(phone);
    }

    public TlgClientEntity addClient(TlgClientEntity tlgClientEntity) {
        TlgClientEntity client;
        client = getByPhone(tlgClientEntity.getPhone());
        if (client == null) {
            client = tlgClientRepo.save(tlgClientEntity);
        }
        return client;
    }
}
