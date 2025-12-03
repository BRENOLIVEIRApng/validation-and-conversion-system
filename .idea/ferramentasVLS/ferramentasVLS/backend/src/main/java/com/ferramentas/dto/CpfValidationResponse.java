package com.ferramentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpfValidationResponse {
    private boolean valido;
    private String cpfFormatado;
    private String mensagem;
}