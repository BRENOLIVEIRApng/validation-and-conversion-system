package com.ferramentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvProcessingResponse {
    private String fileName;
    private String downloadUrl;
    private int totalLinhas;
    private int linhasProcessadas;
}