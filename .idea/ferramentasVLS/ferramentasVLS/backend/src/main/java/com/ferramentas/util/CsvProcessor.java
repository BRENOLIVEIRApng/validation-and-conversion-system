package com.ferramentas.util;

import com.ferramentas.exception.FileProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvProcessor {

    private final CpfValidator cpfValidator;
    private final DateFormatter dateFormatter;

    private static final Set<String> ALLOWED_SPACE_FIELDS = Set.of(
            "NOME", "PAI", "MAE", "DEPARTAMENTO",
            "DESCRICAO_CENTRO_CUSTO", "DESCRICAO_ORGAO",
            "DESCRICAO_REGIONAL", "DESCRICAO_SETOR",
            "CARGO", "REFERENCIA", "OBSERVACAO"
    );

    public File processCsv(File inputFile) {
        try (Reader reader = new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            List<String> headers = new ArrayList<>(parser.getHeaderNames());
            List<Map<String, String>> processedRows = new ArrayList<>();

            for (CSVRecord record : parser) {
                Map<String, String> processedRow = processRecord(record, headers);
                processedRows.add(processedRow);
            }

            List<String> nonEmptyColumns = getNonEmptyColumns(headers, processedRows);
            return writeCsv(processedRows, nonEmptyColumns);

        } catch (IOException e) {
            log.error("Erro ao processar CSV", e);
            throw new FileProcessingException("Erro ao processar arquivo CSV", e);
        }
    }

    private Map<String, String> processRecord(CSVRecord record, List<String> headers) {
        Map<String, String> processed = new HashMap<>();

        for (String header : headers) {
            String value = record.get(header);
            String headerUpper = header.toUpperCase();

            if ("CPF".equals(headerUpper)) {
                String cleanCpf = cpfValidator.cleanCpf(value);
                processed.put(header, cpfValidator.isValid(cleanCpf) ? cleanCpf : "");
            } else if ("NASCIMENTO".equals(headerUpper) || "ADMISSAO".equals(headerUpper)) {
                try {
                    processed.put(header, dateFormatter.convertToBrazilianFormat(value));
                } catch (Exception e) {
                    processed.put(header, "");
                }
            } else if ("DEPENDENTE".equals(headerUpper)) {
                processed.put(header, normalizeDependente(value));
            } else {
                processed.put(header, sanitizeField(value, headerUpper));
            }
        }

        return processed;
    }

    private String sanitizeField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        String sanitized = value.trim()
                .replace("'", "")
                .replace("'", "")
                .replace("'", "")
                .replace("'", "");

        if (ALLOWED_SPACE_FIELDS.contains(fieldName)) {
            sanitized = sanitized.replaceAll("[^A-Za-z0-9\\s]", "");
            sanitized = sanitized.replaceAll("\\s+", " ").trim();
        } else {
            sanitized = sanitized.replaceAll("[^A-Za-z0-9]", "");
        }

        return sanitized;
    }

    private String normalizeDependente(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String lower = value.trim().toLowerCase();
        if (lower.contains("depend")) return "S";
        if (lower.contains("titular")) return "N";
        if ("s".equals(lower) || "n".equals(lower)) return lower.toUpperCase();
        return "";
    }

    private List<String> getNonEmptyColumns(List<String> headers, List<Map<String, String>> rows) {
        List<String> nonEmpty = new ArrayList<>();
        for (String header : headers) {
            boolean hasContent = rows.stream()
                    .anyMatch(row -> {
                        String value = row.get(header);
                        return value != null && !value.trim().isEmpty();
                    });
            if (hasContent) {
                nonEmpty.add(header);
            }
        }
        return nonEmpty;
    }

    private File writeCsv(List<Map<String, String>> rows, List<String> columns) throws IOException {
        File outputFile = File.createTempFile("formatado_", ".csv");

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader(columns.toArray(new String[0]))
                             .withDelimiter(';'))) {

            writer.write('\ufeff');

            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>();
                for (String column : columns) {
                    values.add(row.getOrDefault(column, ""));
                }
                printer.printRecord(values);
            }
        }

        return outputFile;
    }
}