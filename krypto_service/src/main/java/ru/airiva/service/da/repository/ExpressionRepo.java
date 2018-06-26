package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.ExpressionEntity;

/**
 * @author Ivan
 */
@Repository
public interface ExpressionRepo extends JpaRepository<ExpressionEntity, Long> {
}
