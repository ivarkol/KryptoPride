package ru.airiva.service.fg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.airiva.entities.PersonEntity;
import ru.airiva.entities.TlgClientEntity;
import ru.airiva.enums.KryptoRole;
import ru.airiva.service.da.repository.PersonRepo;

import java.util.HashSet;
import java.util.Optional;
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
        Optional<PersonEntity> opt = personRepo.findById(1L);
        PersonEntity person;
        if (!opt.isPresent()) {
            person = new PersonEntity();
            person.setId(1L);
            person.setPassword("admin");
            person.setUsername("admin");
            person.setRole(KryptoRole.ADMIN);
            person.setPaymentAddress("payment address");
            person = personRepo.save(person);
        } else {
            person = opt.get();
        }
        person.getClients().add(tlgClientEntity);
        return personRepo.saveAndFlush(person);
    }
}
