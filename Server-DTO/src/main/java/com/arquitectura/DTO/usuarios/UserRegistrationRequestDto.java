package com.arquitectura.DTO.usuarios;

public class UserRegistrationRequestDto {
    private String username;
    private String email;
    private String password; // Contraseña en texto plano

    // Constructor vacío
    public UserRegistrationRequestDto() {}

    // Constructor con campos
    public UserRegistrationRequestDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}