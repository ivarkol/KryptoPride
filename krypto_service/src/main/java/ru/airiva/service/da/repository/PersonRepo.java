package ru.airiva.service.da.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.airiva.entities.PersonEntity;

/**
 * @author Ivan
 */
@Repository
public interface PersonRepo extends JpaRepository<PersonEntity, Long> {
}
