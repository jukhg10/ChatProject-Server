package com.arquitectura.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// 👇 ¡ESTA ES LA LÍNEA MÁGICA! 👇
// Le dice a Spring que escanee todos los subpaquetes de 'com.arquitectura'
// para encontrar beans como @Service, @Component, @Controller, etc.
@ComponentScan(basePackages = "com.arquitectura")
public class LogicConfig {
}