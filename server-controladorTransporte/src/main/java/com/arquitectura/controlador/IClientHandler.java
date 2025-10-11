package com.arquitectura.controlador;

public interface IClientHandler {
    void sendMessage(String message);
    String getClientIpAddress();
}