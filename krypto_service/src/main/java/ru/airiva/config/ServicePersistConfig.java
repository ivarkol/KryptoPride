package ru.airiva.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.airiva.properties.DbProperties;

import java.beans.PropertyVetoException;
import java.util.Properties;

/**
 * @author Ivan
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {"ru.airiva.dao"},
        entityManagerFactoryRef = "emf",
        transactionManagerRef = "jpaTransactionManager")
@EnableTransactionManagement
@PropertySource(value = "classpath:db.properties", encoding = "UTF-8")
public class ServicePersistConfig {

    private DbProperties dbProperties;

    @Autowired
    public void setDbProperties(DbProperties dbProperties) {
        this.dbProperties = dbProperties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean emf() throws PropertyVetoException {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setJpaVendorAdapter(jpaVendorAdapter());
        emf.setPackagesToScan("ru.airiva.entities");
        Properties hibernateProps = new Properties();
        hibernateProps.setProperty("hibernate.hbm2ddl.auto", dbProperties.hbm2dllAuto);
        hibernateProps.setProperty("hibernate.jdbc.lob.non_contextual_creation", dbProperties.lobNonContextualCreation);
        emf.setJpaProperties(hibernateProps);
        return emf;
    }

    @Bean(destroyMethod = "close")
    public ComboPooledDataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(dbProperties.driver);
        dataSource.setJdbcUrl(dbProperties.url);
        dataSource.setUser(dbProperties.username);
        dataSource.setPassword(dbProperties.password);
        dataSource.setInitialPoolSize(dbProperties.poolInitial);
        dataSource.setMinPoolSize(dbProperties.poolMin);
        dataSource.setMaxPoolSize(dbProperties.poolMax);
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        jpaVendorAdapter.setDatabase(Database.valueOf(dbProperties.dbms));
        jpaVendorAdapter.setDatabasePlatform(dbProperties.dialect);
        return jpaVendorAdapter;
    }

}
