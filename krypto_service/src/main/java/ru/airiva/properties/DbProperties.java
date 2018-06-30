package ru.airiva.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Ivan
 */
@Component
public class DbProperties {

    @Value("${db.url}")
    public String url;

    @Value("${db.username}")
    public String username;

    @Value("${db.password}")
    public String password;

    @Value("${db.driver}")
    public String driver;

    @Value("${db.dialect}")
    public String dialect;

    @Value("${db.pool.initial}")
    public Integer poolInitial;

    @Value("${db.pool.min}")
    public Integer poolMin;

    @Value("${db.pool.max}")
    public Integer poolMax;

    @Value("${db.dbms}")
    public String dbms;

    @Value("${db.hibernate.hbm2ddl.auto}")
    public String hbm2dllAuto;

    @Value("${db.hibernate.jdbc.lob.non_contextual_creation}")
    public String lobNonContextualCreation;
}
