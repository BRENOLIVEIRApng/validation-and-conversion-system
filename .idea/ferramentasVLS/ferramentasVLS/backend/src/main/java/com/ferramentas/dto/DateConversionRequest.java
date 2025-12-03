package com.ferramentas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateConversionRequest {
    @NotBlank(message = "Datas não podem estar vazias")
    private String datas;
}
