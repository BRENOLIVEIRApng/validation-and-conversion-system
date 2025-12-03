package com.ferramentas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpfValidationRequest {
    @NotBlank(message = "CPF não pode estar vazio")
    private String cpf;
}
