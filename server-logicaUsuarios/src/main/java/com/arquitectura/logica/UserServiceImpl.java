package com.arquitectura.logica;

import com.arquitectura.DTO.usuarios.UserRegistrationRequestDto;
import com.arquitectura.DTO.usuarios.UserResponseDto;
import com.arquitectura.domain.User;
import com.arquitectura.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // 1. Marca esta clase como un componente de servicio de Spring.
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 2. Inyección de dependencias por constructor. Spring nos pasará las instancias automáticamente.
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Usamos un codificador de contraseñas robusto.
    }

    @Override
    @Transactional
    public UserResponseDto registrarUsuario(UserRegistrationRequestDto requestDto, String ipAddress) throws Exception {
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new Exception("El nombre de usuario ya está en uso.");
        }
        // Aquí podrías añadir la validación de email si la necesitas.

        String hashedPassword = passwordEncoder.encode(requestDto.getPassword());

        // --- Estrategia de Mapeo: DTO -> Entidad ---
        User newUserEntity = new User();
        newUserEntity.setUsername(requestDto.getUsername());
        newUserEntity.setEmail(requestDto.getEmail());
        newUserEntity.setHashedPassword(hashedPassword);
        newUserEntity.setIpAddress(ipAddress);
        // El campo 'photoAddress' se puede establecer aquí si viene en el DTO o más tarde.

        User savedUser = userRepository.save(newUserEntity);

        // --- Estrategia de Mapeo: Entidad -> DTO ---
        return new UserResponseDto(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getPhotoAddress()
        );
    }
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
}