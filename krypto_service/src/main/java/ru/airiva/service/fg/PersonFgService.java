package ru.airiva.service.fg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.airiva.entities.PersonEntity;
import ru.airiva.entities.TlgClientEntity;
import ru.airiva.service.da.repository.PersonRepo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan
 */
@Service
public class PersonFgService {

    private PersonRepo personRepo;

    @Autowired
    public void setPersonRepo(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Transactional
    public Set<TlgClientEntity> clientsByPersonId(Long personId) {
        Set<TlgClientEntity> clients = new HashSet<>();
        personRepo.findById(personId).ifPresent(person -> clients.addAll(person.getClients()));
        return clients;
    }

    public PersonEntity getById(Long personId) {
        return personRepo.findById(personId).orElse(null);
    }

    @Transactional
    public PersonEntity updatePerson(TlgClientEntity tlgClientEntity) {
        PersonEntity personEntity = personRepo.findById(1L).get();
        Set<TlgClientEntity> clients = personEntity.getClients();
        if (clients == null) {
            clients = new HashSet<>();
        }
        clients.add(tlgClientEntity);
        return personRepo.saveAndFlush(personEntity);
    }
}
