package com.testai.projectservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path rootLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    private String noSpaces(String s) {
        return s.replaceAll(" ", "_");
    }

    private String removeSpecialCharacters(String str) {
        String result = noSpaces(str);
        return result.replaceAll("[^a-zA-Z0-9]", "");
    }

    public String store(MultipartFile file, String project_name) {
        try {
            String filename = removeSpecialCharacters(project_name) + "_" + noSpaces(file.getOriginalFilename());
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    public void delete(String filename) {
        try {
            Path filePath = rootLocation.resolve(filename).normalize().toAbsolutePath();
            Path rootPath = rootLocation.toAbsolutePath().normalize();

            if (!filePath.startsWith(rootPath)) {
                throw new RuntimeException("Cannot delete file outside the storage directory");
            }

            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

}
