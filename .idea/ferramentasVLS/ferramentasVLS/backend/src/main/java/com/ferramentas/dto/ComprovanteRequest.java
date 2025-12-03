package com.ferramentas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprovanteRequest {
    @NotBlank(message = "JSON de retorno não pode estar vazio")
    private String jsonRetorno;
}
