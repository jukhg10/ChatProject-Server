package com.arquitectura;

import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.config.LogicConfig;
import com.arquitectura.configdb.ConfiguracionPersistencia;
import com.arquitectura.logica.IUserService;
import com.arquitectura.mail.MailConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserLogicTest {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBA DE LÓGICA DE USUARIO ---");

        // 1. Inicia el contexto de Spring cargando AMBAS configuraciones.
        // Spring necesita conocer los beans de persistencia y los de correo.
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ConfiguracionPersistencia.class,
                MailConfig.class,
                LogicConfig.class
        );

        System.out.println("[OK] Contexto de Spring cargado con Persistencia y Mail.");

        try {
            // 2. Obtiene el bean del servicio de usuario.
            IUserService userService = context.getBean(IUserService.class);
            System.out.println("[OK] IUserService obtenido del contexto.");

            // --- ESCENARIO DE PRUEBA ---

            // 3. Prepara el DTO con los datos del nuevo usuario a registrar.
            System.out.println("\n-> Preparando datos para registrar nuevo usuario...");
            UserRegistrationRequestDto newUserDto = new UserRegistrationRequestDto(
                    "testUserFromLogic",
                    "nslozano@unillanos.edu.co", // Usa un email real al que tengas acceso para ver el correo
                    "unaClaveSegura123"
            );
            String clientIp = "192.168.1.100";

            // 4. Llama al método de la lógica de negocio.
            System.out.println("-> Ejecutando userService.registrarUsuario()...");
            UserResponseDto responseDto = userService.registrarUsuario(newUserDto, clientIp);

            // 5. Verifica los resultados.
            if (responseDto != null && responseDto.getUsername().equals(newUserDto.getUsername())) {
                System.out.println("[ÉXITO] Usuario registrado correctamente con ID: " + responseDto.getUserId());
                System.out.println("[VERIFICAR] Revisa la bandeja de entrada de '" + responseDto.getEmail() + "' para confirmar el correo.");
            } else {
                System.err.println("ERROR: El registro de usuario no devolvió una respuesta válida.");
            }

            System.out.println("\n--- PRUEBA DE LÓGICA FINALIZADA CON ÉXITO ---");

        } catch (Exception e) {
            System.err.println("!!! FALLÓ LA PRUEBA DE LÓGICA !!!");
            e.printStackTrace();
        } finally {
            // 6. Cierra el contexto de Spring.
            context.close();
        }
    }
}