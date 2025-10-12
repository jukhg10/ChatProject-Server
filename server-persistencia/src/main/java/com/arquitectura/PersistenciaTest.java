package com.arquitectura;

import com.arquitectura.configdb.ConfiguracionPersistencia;
import com.arquitectura.domain.User;
import com.arquitectura.persistence.UserRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PersistenciaTest {

    public static void main(String[] args) {
        System.out.println("Iniciando prueba de persistencia...");

        // 1. Inicia el contexto de Spring cargando ÚNICAMENTE tu clase de configuración de persistencia.
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfiguracionPersistencia.class);

        System.out.println("Contexto de Spring cargado exitosamente.");

        // 2. Pide a Spring que te dé el "bean" del UserRepository que creó automáticamente.
        UserRepository userRepository = context.getBean(UserRepository.class);
        System.out.println("UserRepository obtenido del contexto.");

        // 3. ¡A PRUEBA! Vamos a crear y guardar un nuevo usuario.
        System.out.println("Intentando guardar un nuevo usuario...");
        try {
            User newUser = new User("testUser", "test@example.com", "hashedPassword123", "127.0.0.1");
            User savedUser = userRepository.save(newUser);

            System.out.println("¡ÉXITO! Usuario guardado correctamente con ID: " + savedUser.getId());

            // 4. Verifiquemos que podemos recuperarlo.
            System.out.println("Verificando si podemos encontrar el usuario guardado...");
            User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

            if (foundUser != null) {
                System.out.println("¡ÉXITO! Usuario encontrado en la base de datos: " + foundUser.getUsername());
            } else {
                System.err.println("ERROR: No se pudo encontrar el usuario después de guardarlo.");
            }

        } catch (Exception e) {
            System.err.println("!!! FALLO LA PRUEBA DE PERSISTENCIA !!!");
            e.printStackTrace();
        }

        // 5. Cierra el contexto de Spring para liberar recursos.
        context.close();
    }
}