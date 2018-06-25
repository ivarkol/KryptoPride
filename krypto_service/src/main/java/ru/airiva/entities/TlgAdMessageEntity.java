package ru.airiva.entities;

import javax.persistence.*;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_AD_MESSAGES)
@SequenceGenerator(name = TLG_AD_MESSAGES_GEN, sequenceName = TLG_AD_MESSAGES_SEQ, allocationSize = 1)
public class TlgAdMessageEntity {

    @Id
    @GeneratedValue(generator = TLG_AD_MESSAGES_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "message")
    private String message;

}
