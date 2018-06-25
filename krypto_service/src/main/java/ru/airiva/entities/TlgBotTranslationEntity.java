package ru.airiva.entities;

import javax.persistence.*;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_BOT_TRANSLATIONS)
@SequenceGenerator(name = TLG_BOT_TRANSLATIONS_GEN, sequenceName = TLG_BOT_TRANSLATIONS_SEQ, allocationSize = 1)
public class TlgBotTranslationEntity {

    @Id
    @GeneratedValue(generator = TLG_BOT_TRANSLATIONS_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    //TODO реализовать equals&hashcode
}
