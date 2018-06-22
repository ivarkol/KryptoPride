package ru.airiva.entities;

import javax.persistence.*;

import java.util.List;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */

@Entity
@Table(name = PERSON)
@SequenceGenerator(name = PERSON_GEN, sequenceName = PERSON_SEQ, allocationSize = 1)
public class PersonEntity {

    @Id
    @GeneratedValue(generator = PERSON_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany
    @JoinColumn(name = "client_id")
    private List<TlgClientEntity> clients;

    @OneToOne
    @JoinColumn(name = "bot_id")
    private TlgBotEntity bot;





}
