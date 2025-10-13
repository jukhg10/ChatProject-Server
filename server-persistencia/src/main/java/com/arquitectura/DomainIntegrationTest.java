package com.arquitectura;

import com.arquitectura.configdb.ConfiguracionPersistencia;
import com.arquitectura.domain.*;
import com.arquitectura.domain.enums.EstadoMembresia;
import com.arquitectura.domain.enums.TipoCanal;
import com.arquitectura.persistence.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DomainIntegrationTest {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBA DE INTEGRACIÓN DEL DOMINIO ---");

        // 1. Inicia el contexto de Spring (igual que antes)
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfiguracionPersistencia.class);
        System.out.println("[OK] Contexto de Spring cargado.");

        try {
            // 2. Obtiene todos los repositorios que vamos a necesitar
            UserRepository userRepository = context.getBean(UserRepository.class);
            ChannelRepository channelRepository = context.getBean(ChannelRepository.class);
            MessageRepository messageRepository = context.getBean(MessageRepository.class);
            MembresiaCanalRepository membresiaRepository = context.getBean(MembresiaCanalRepository.class);
            TranscripcionAudioRepository transcripcionRepository = context.getBean(TranscripcionAudioRepository.class);
            System.out.println("[OK] Repositorios obtenidos.");

            // --- ESCENARIO DE PRUEBA ---

            // 3. Crear y guardar dos usuarios
            System.out.println("\n-> Creando usuarios...");
            User owner = new User("ownerUser", "owner@chat.com", "pass123", "192.168.1.1");
            User member = new User("memberUser", "member@chat.com", "pass456", "192.168.1.2");
            userRepository.save(owner);
            userRepository.save(member);
            System.out.println("[OK] Usuarios guardados con IDs: " + owner.getUserId() + " y " + member.getUserId());

            // 4. Crear un canal de grupo
            System.out.println("\n-> Creando un canal...");
            // Asumiendo que has corregido el constructor de Channel para aceptar el tipo
            Channel groupChannel = new Channel(owner.getUsername() + "'s Group", owner, TipoCanal.GRUPO);
            channelRepository.save(groupChannel);
            System.out.println("[OK] Canal guardado con ID: " + groupChannel.getChannelId());

            // 5. Añadir el segundo usuario al canal usando la entidad MembresiaCanal
            System.out.println("\n-> Añadiendo miembro al canal...");
            MembresiaCanalId membresiaId = new MembresiaCanalId( groupChannel.getChannelId(),member.getUserId());
            MembresiaCanal membresia = new MembresiaCanal(membresiaId, member, groupChannel, EstadoMembresia.ACTIVO);
            membresiaRepository.save(membresia);
            System.out.println("[OK] Miembro añadido al canal con estado: " + membresia.getEstado());

            // 6. Enviar un mensaje de texto al canal
            System.out.println("\n-> Enviando mensaje de texto...");
            TextMessage textMessage = new TextMessage(owner, groupChannel, "¡Hola a todos en el grupo!");
            messageRepository.save(textMessage);
            System.out.println("[OK] Mensaje de texto guardado con ID: " + textMessage.getIdMensaje());

            // 7. Enviar un mensaje de audio y su transcripción
            System.out.println("\n-> Enviando mensaje de audio...");
            AudioMessage audioMessage = new AudioMessage(member, groupChannel, "/audio_files/bienvenida.mp3");
            messageRepository.save(audioMessage);
            System.out.println("[OK] Mensaje de audio guardado con ID: " + audioMessage.getIdMensaje());

            System.out.println("\n-> Creando transcripción...");
            TranscripcionAudio transcripcion = new TranscripcionAudio(audioMessage, "Bienvenida al canal de pruebas.");
            transcripcionRepository.save(transcripcion);
            System.out.println("[OK] Transcripción guardada para el mensaje ID: " + transcripcion.getId());


            System.out.println("\n--- PRUEBA FINALIZADA CON ÉXITO ---");

        } catch (Exception e) {
            System.err.println("!!! FALLÓ LA PRUEBA DE INTEGRACIÓN !!!");
            e.printStackTrace();
        } finally {
            // 8. Cierra el contexto de Spring
            context.close();
        }
    }
}