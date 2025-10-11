package com.arquitectura.logica;

import com.arquitectura.domain.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    /**
     * Registra un nuevo usuario en el sistema.
     * Se encarga de hashear la contraseña antes de guardarla.
     * @param username El nombre de usuario.
     * @param email El email.
     * @param password La contraseña en texto plano.
     * @param ipAddress La dirección IP del cliente.
     * @return El usuario guardado.
     * @throws Exception si el nombre de usuario o email ya existen.
     */
    User registrarUsuario(String username, String email, String password, String ipAddress) throws Exception;

    /**
     * Busca un usuario por su nombre de usuario.
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    Optional<User> buscarPorUsername(String username);

    /**
     * Obtiene una lista de todos los usuarios registrados.
     * @return Una lista de todos los usuarios.
     */
    List<User> obtenerTodosLosUsuarios();
}