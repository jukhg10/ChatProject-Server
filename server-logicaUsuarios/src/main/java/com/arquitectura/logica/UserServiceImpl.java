package com.arquitectura.logica;

import com.arquitectura.domain.User;
import com.arquitectura.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public User registrarUsuario(String username, String email, String password, String ipAddress) throws Exception {
        // 3. Verificamos que el usuario no exista.
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("El nombre de usuario ya está en uso.");
        }

        // 4. Hasheamos la contraseña antes de guardarla. ¡Nunca guardes contraseñas en texto plano!
        String hashedPassword = passwordEncoder.encode(password);

        // 5. Creamos la nueva entidad User.
        User newUser = new User(username, email, hashedPassword, ipAddress);

        // 6. Usamos el repositorio para guardar el usuario en la base de datos.
        return userRepository.save(newUser);
    }
    @Override
public List<User> obtenerTodosLosUsuarios() {
    return userRepository.findAll(); // Simplemente llamamos al método que nos da el repositorio.
}
    @Override
    public Optional<User> buscarPorUsername(String username) {
        return userRepository.findByUsername(username);
    }
}