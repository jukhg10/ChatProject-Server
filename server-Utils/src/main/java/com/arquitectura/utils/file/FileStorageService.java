package com.arquitectura.utils.file;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path rootStorageLocation;

    public FileStorageService() {
        // Todos los archivos se guardarán dentro de una carpeta principal llamada "storage".
        this.rootStorageLocation = Paths.get("storage").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento raíz.", ex);
        }
    }

    /**
     * Guarda un archivo en un subdirectorio específico.
     *
     * @param sourceFile   El archivo original a guardar.
     * @param newFileName  El nuevo nombre para el archivo (sin extensión).
     * @param subDirectory El subdirectorio donde se guardará (ej. "user_photos", "audio_files").
     * @return La ruta relativa del archivo guardado para almacenarla en la base de datos.
     * @throws IOException Si ocurre un error durante la copia del archivo.
     */
    public String storeFile(File sourceFile, String newFileName, String subDirectory) throws IOException {
        // 1. Determinar la extensión del archivo original
        String originalFileName = sourceFile.getName();
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i); // Incluye el punto, ej: ".jpg", ".wav"
        }

        String finalFileName = newFileName + extension;

        // 2. Resolver la ruta del subdirectorio de destino y crearlo si no existe
        Path targetDirectory = this.rootStorageLocation.resolve(subDirectory).normalize();
        Files.createDirectories(targetDirectory); // Crea la carpeta si no existe

        // 3. Resolver la ruta completa del archivo final
        Path targetLocation = targetDirectory.resolve(finalFileName);

        // 4. Copiar el archivo al directorio de destino, reemplazándolo si ya existe
        Files.copy(sourceFile.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // 5. Devolver la ruta relativa (incluyendo el subdirectorio) para guardarla en la BD
        // Esto devolverá algo como "user_photos/juan.jpg" o "audio_files/12345.wav"
        return Paths.get(subDirectory).resolve(finalFileName).toString();
    }
}