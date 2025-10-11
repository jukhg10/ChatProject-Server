package com.arquitectura.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration // 1. Marca esta clase como una fuente de configuración para Spring.
@EnableJpaRepositories(basePackages = "com.arquitectura.persistence") // 2. ¡La clave! Activa Spring Data JPA y le dice dónde buscar tus interfaces de Repositorio.
public class PersistenceConfig {
    // Esta clase puede estar vacía por ahora.
    // Su propósito principal es tener las anotaciones de configuración.
    // Spring leerá automáticamente el archivo application.properties y configurará
    // el DataSource y el EntityManagerFactory por nosotros.
}