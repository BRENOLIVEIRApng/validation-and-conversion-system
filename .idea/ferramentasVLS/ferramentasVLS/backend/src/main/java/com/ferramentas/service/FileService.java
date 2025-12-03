package com.ferramentas.service;

import com.ferramentas.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Diretório de arquivos criado: {}", this.fileStorageLocation);
        } catch (IOException e) {
            throw new FileProcessingException("Não foi possível criar diretório de upload", e);
        }
    }

    public File saveFile(File file, String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Arquivo salvo: {}", fileName);
            return targetLocation.toFile();
        } catch (IOException e) {
            throw new FileProcessingException("Erro ao salvar arquivo: " + fileName, e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileProcessingException("Arquivo não encontrado: " + fileName);
            }
        } catch (Exception e) {
            throw new FileProcessingException("Erro ao carregar arquivo: " + fileName, e);
        }
    }
}