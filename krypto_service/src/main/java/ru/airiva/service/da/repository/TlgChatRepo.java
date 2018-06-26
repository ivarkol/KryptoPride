package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.TlgChatEntity;

/**
 * @author Ivan
 */
@Repository
public interface TlgChatRepo extends JpaRepository<TlgChatEntity, Long> {
}
