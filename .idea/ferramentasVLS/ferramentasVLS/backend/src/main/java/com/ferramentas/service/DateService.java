package com.ferramentas.service;

import com.ferramentas.dto.DateConversionRequest;
import com.ferramentas.dto.DateConversionResponse;
import com.ferramentas.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DateService {

    private final DateFormatter dateFormatter;

    public DateConversionResponse convertDates(DateConversionRequest request) {
        String datas = request.getDatas();

        if (datas == null || datas.trim().isEmpty()) {
            log.warn("Nenhuma data fornecida para conversão");
            return DateConversionResponse.builder()
                    .datasConvertidas(List.of())
                    .erros(List.of("Nenhuma data fornecida"))
                    .totalConvertidas(0)
                    .totalErros(1)
                    .build();
        }

        List<String> dateList = Arrays.stream(datas.split("\\r?\\n"))
                .map(String::trim)
                .filter(d -> !d.isEmpty())
                .toList();

        List<String> convertedDates = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String dateStr : dateList) {
            try {
                String converted = dateFormatter.convertToBrazilianFormat(dateStr);
                convertedDates.add(converted);
                log.debug("Data convertida: {} -> {}", dateStr, converted);
            } catch (Exception e) {
                String errorMsg = "DATA INVÁLIDA: " + dateStr;
                errors.add(errorMsg);
                log.warn("Erro ao converter data: {}", dateStr);
            }
        }

        log.info("Conversão de datas concluída - Sucesso: {}, Erros: {}",
                convertedDates.size(), errors.size());

        return DateConversionResponse.builder()
                .datasConvertidas(convertedDates)
                .erros(errors)
                .totalConvertidas(convertedDates.size())
                .totalErros(errors.size())
                .build();
    }
}
