package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.TlgChatPairEntity;

/**
 * @author Ivan
 */
@Repository
public interface TlgChatPairRepo extends JpaRepository<TlgChatPairEntity, Long> {
}
