package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.TlgAdEntity;

/**
 * @author Ivan
 */
@Repository
public interface TlgAdRepo extends JpaRepository<TlgAdEntity, Long> {
}
