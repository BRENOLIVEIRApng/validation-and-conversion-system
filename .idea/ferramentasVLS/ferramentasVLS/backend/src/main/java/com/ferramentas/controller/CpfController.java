package com.ferramentas.controller;

import com.ferramentas.dto.ApiResponse;
import com.ferramentas.dto.CpfValidationRequest;
import com.ferramentas.dto.CpfValidationResponse;
import com.ferramentas.service.CpfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cpf")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CpfController {

    private final CpfService cpfService;

    @PostMapping("/validar")
    public ResponseEntity<ApiResponse<CpfValidationResponse>> validateCpf(
            @Valid @RequestBody CpfValidationRequest request) {

        log.info("Recebida requisição de validação de CPF");
        CpfValidationResponse response = cpfService.validateCpf(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}