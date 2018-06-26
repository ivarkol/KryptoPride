package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.TlgClientEntity;

/**
 * @author Ivan
 */
@Repository
public interface TlgClientRepo extends JpaRepository<TlgClientEntity, Long> {
}
