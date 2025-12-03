package com.ferramentas.controller;

import com.ferramentas.dto.ApiResponse;
import com.ferramentas.dto.DateConversionRequest;
import com.ferramentas.dto.DateConversionResponse;
import com.ferramentas.service.DateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/datas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DateController {

    private final DateService dateService;

    @PostMapping("/converter")
    public ResponseEntity<ApiResponse<DateConversionResponse>> convertDates(
            @Valid @RequestBody DateConversionRequest request) {

        log.info("Recebida requisição de conversão de datas");
        DateConversionResponse response = dateService.convertDates(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}