package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.airiva.entities.TlgTrPackageEntity;

import java.util.Set;

/**
 * @author Ivan
 */
public interface TlgTrPackageRepo extends JpaRepository<TlgTrPackageEntity, Long> {

    Set<TlgTrPackageEntity> findByPersonEntityId(Long id);

}
