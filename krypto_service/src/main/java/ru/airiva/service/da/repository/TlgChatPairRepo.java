package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.TlgChatPairEntity;

import java.util.Set;

/**
 * @author Ivan
 */
@Repository
public interface TlgChatPairRepo extends JpaRepository<TlgChatPairEntity, Long> {

    Set<TlgChatPairEntity> getTlgChatPairEntitiesByTlgClientEntity_PhoneAndTlgTrPackageEntity_Enabled(String phone, boolean enabled);

    TlgChatPairEntity getTlgChatPairEntityBySrcChat_TlgChatIdAndDestChat_TlgChatIdAndTlgClientEntity_PhoneAndTlgTrPackageEntity_Enabled(long srcChatId, long destChatId, String phone, boolean enabled);

}
