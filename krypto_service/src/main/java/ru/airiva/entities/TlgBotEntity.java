package ru.airiva.entities;

import javax.persistence.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = "bot")
@SequenceGenerator(name = "bot_gen", sequenceName = "bot_seq", allocationSize = 1)
public class TlgBotEntity {

    @Id
    @GeneratedValue
    private Long id;

}
