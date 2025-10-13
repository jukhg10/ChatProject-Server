package com.arquitectura.logica;

import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.domain.User;
import com.arquitectura.mail.EmailService;
import com.arquitectura.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.emailService = emailService;
    }

    // Este método ahora recibe y devuelve DTOs
    @Override
    @Transactional
    public UserResponseDto registrarUsuario(UserRegistrationRequestDto requestDto, String ipAddress) throws Exception {
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new Exception("El nombre de usuario ya está en uso.");
        }

        String hashedPassword = passwordEncoder.encode(requestDto.getPassword());

        // Mapeo: DTO -> Entidad
        User newUserEntity = new User(
            requestDto.getUsername(),
            requestDto.getEmail(),
            hashedPassword,
            ipAddress
        );

        User savedUser = userRepository.save(newUserEntity);
        
        emailService.enviarCredenciales(savedUser.getEmail(), savedUser.getUsername(), requestDto.getPassword());

        // Mapeo: Entidad -> DTO para la respuesta
        return new UserResponseDto(
            savedUser.getUserId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getPhotoAddress()
        );
    }

    // Devuelve una lista de DTOs
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> obtenerTodosLosUsuarios() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhotoAddress()))
                .collect(Collectors.toList());
    }

    // Devuelve un DTO opcional
    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> buscarPorUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserResponseDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhotoAddress()));
    }

    // Devuelve la entidad para uso interno
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findEntityById(int id) {
        return userRepository.findById(id);
    }
}