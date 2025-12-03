package com.ferramentas.service;

import com.ferramentas.dto.CpfValidationRequest;
import com.ferramentas.dto.CpfValidationResponse;
import com.ferramentas.exception.InvalidCpfException;
import com.ferramentas.util.CpfValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CpfService {

    private final CpfValidator cpfValidator;

    public CpfValidationResponse validateCpf(CpfValidationRequest request) {
        String cpf = request.getCpf();

        if (cpf == null || cpf.trim().isEmpty()) {
            throw new InvalidCpfException("CPF não pode estar vazio");
        }

        String cleanCpf = cpfValidator.cleanCpf(cpf);

        if (cleanCpf.length() != 11) {
            log.debug("CPF inválido - tamanho incorreto: {}", cpf);
            return CpfValidationResponse.builder()
                    .valido(false)
                    .cpfFormatado(cpf)
                    .mensagem("CPF deve conter 11 dígitos")
                    .build();
        }

        if (cleanCpf.matches("(\\d)\\1{10}")) {
            log.debug("CPF inválido - dígitos repetidos: {}", cpf);
            return CpfValidationResponse.builder()
                    .valido(false)
                    .cpfFormatado(cpfValidator.format(cleanCpf))
                    .mensagem("CPF com dígitos repetidos é inválido")
                    .build();
        }

        boolean isValid = cpfValidator.isValid(cleanCpf);

        log.debug("CPF {} - válido: {}", cpf, isValid);

        return CpfValidationResponse.builder()
                .valido(isValid)
                .cpfFormatado(cpfValidator.format(cleanCpf))
                .mensagem(isValid ? "CPF válido ✓" : "CPF inválido ✗")
                .build();
    }
}