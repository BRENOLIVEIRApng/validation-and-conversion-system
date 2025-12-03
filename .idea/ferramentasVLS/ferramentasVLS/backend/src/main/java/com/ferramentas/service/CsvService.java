package com.ferramentas.service;

import com.ferramentas.dto.CsvProcessingResponse;
import com.ferramentas.exception.FileProcessingException;
import com.ferramentas.util.CsvProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvService {

    private final CsvProcessor csvProcessor;
    private final FileService fileService;

    public CsvProcessingResponse processCsv(MultipartFile file) {
        validateFile(file);

        File tempFile = null;
        File processedFile = null;

        try {
            tempFile = convertMultipartToFile(file);

            log.info("Processando CSV: {}", file.getOriginalFilename());
            processedFile = csvProcessor.processCsv(tempFile);

            String fileName = "formatado_" + UUID.randomUUID() + ".csv";
            File savedFile = fileService.saveFile(processedFile, fileName);

            long lineCount = Files.lines(savedFile.toPath()).count() - 1;

            log.info("CSV processado com sucesso: {} linhas", lineCount);

            return CsvProcessingResponse.builder()
                    .fileName(fileName)
                    .downloadUrl("/files/" + fileName)
                    .totalLinhas((int) lineCount)
                    .linhasProcessadas((int) lineCount)
                    .build();

        } catch (Exception e) {
            log.error("Erro ao processar CSV", e);
            throw new FileProcessingException("Erro ao processar arquivo CSV: " + e.getMessage(), e);
        } finally {
            deleteTempFiles(tempFile, processedFile);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileProcessingException("Arquivo não pode estar vazio");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new FileProcessingException("Apenas arquivos CSV são permitidos");
        }
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload_", ".csv");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    private void deleteTempFiles(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    log.warn("Não foi possível deletar arquivo temporário: {}", file.getName());
                }
            }
        }
    }
}