package com.ferramentas.controller;

import com.ferramentas.dto.ApiResponse;
import com.ferramentas.dto.CsvProcessingResponse;
import com.ferramentas.service.CsvService;
import com.ferramentas.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/csv")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CsvController {

    private final CsvService csvService;
    private final FileService fileService;

    @PostMapping("/processar")
    public ResponseEntity<ApiResponse<CsvProcessingResponse>> processCsv(
            @RequestParam("file") MultipartFile file) {

        log.info("Recebida requisição de processamento de CSV: {}", file.getOriginalFilename());
        CsvProcessingResponse response = csvService.processCsv(file);

        return ResponseEntity.ok(ApiResponse.success("CSV processado com sucesso", response));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        log.info("Download solicitado: {}", fileName);

        Resource resource = fileService.loadFileAsResource(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}