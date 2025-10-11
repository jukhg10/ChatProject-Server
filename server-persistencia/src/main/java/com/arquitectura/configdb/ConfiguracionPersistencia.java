package com.arquitectura.configdb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
@PropertySource("file:./config/persistencia.properties")
@EnableJpaRepositories(basePackages = "com.arquitectura.configdb")
public class ConfiguracionPersistencia {
    // Spring leerá el valor de 'db.url' del archivo y lo inyectará en esta variable
    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.driver}")
    private String dbDriver;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);


        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource()); // Usa el DataSource
        emf.setPackagesToScan("com.arquitectura."); // Dónde buscar tus clases @Entity
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter()); // Le decimos que usaremos Hibernate
        emf.setJpaProperties(hibernateProperties()); // Le pasamos las propiedades de Hibernate
        return emf;
    }
}
