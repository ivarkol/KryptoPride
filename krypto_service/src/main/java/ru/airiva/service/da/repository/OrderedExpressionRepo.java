package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.OrderedExpressionEntity;

/**
 * @author Ivan
 */
@Repository
public interface OrderedExpressionRepo extends JpaRepository<OrderedExpressionEntity, Long> {
}
