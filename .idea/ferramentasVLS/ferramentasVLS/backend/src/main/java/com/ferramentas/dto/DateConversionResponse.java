package com.ferramentas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateConversionResponse {
    private List<String> datasConvertidas;
    private List<String> erros;
    private int totalConvertidas;
    private int totalErros;
}