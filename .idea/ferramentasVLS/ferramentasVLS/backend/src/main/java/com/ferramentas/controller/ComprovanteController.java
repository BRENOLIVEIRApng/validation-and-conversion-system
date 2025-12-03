package com.ferramentas.controller;

import com.ferramentas.dto.ApiResponse;
import com.ferramentas.dto.ComprovanteRequest;
import com.ferramentas.service.ComprovanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comprovante")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComprovanteController {

    private final ComprovanteService comprovanteService;

    @PostMapping("/gerar")
    public ResponseEntity<Resource> generateComprovante(
            @Valid @RequestBody ComprovanteRequest request) {

        log.info("Recebida requisição de geração de comprovante");

        byte[] pdfBytes = comprovanteService.generatePdf(request);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"comprovante.pdf\"")
                .body(resource);
    }
}