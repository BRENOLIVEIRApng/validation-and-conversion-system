package com.ferramentas.service;

import com.ferramentas.dto.ComprovanteRequest;
import com.ferramentas.exception.FileProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ComprovanteService {

    private final Gson gson = new Gson();

    public byte[] generatePdf(ComprovanteRequest request) {
        try {
            String jsonRetorno = request.getJsonRetorno();
            String mensagem = extractMensagem(jsonRetorno);

            return createPdf(mensagem);

        } catch (Exception e) {
            log.error("Erro ao gerar PDF", e);
            throw new FileProcessingException("Erro ao gerar comprovante PDF: " + e.getMessage(), e);
        }
    }

    private String extractMensagem(String jsonRetorno) {
        try {
            Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(jsonRetorno);

            if (matcher.find()) {
                String jsonStr = matcher.group();
                JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
                JsonArray mensagemArray = jsonObject.getAsJsonArray("mensagem");
                JsonObject firstMessage = mensagemArray.get(0).getAsJsonObject();
                return firstMessage.get("mensagem").getAsString();
            }

            throw new IllegalArgumentException("Formato de JSON inválido");
        } catch (Exception e) {
            log.error("Erro ao extrair mensagem do JSON", e);
            throw new FileProcessingException("Formato de JSON inválido. Verifique o conteúdo.", e);
        }
    }

    private byte[] createPdf(String mensagem) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            mensagem = mensagem.replace("\\r\\n", "\n")
                    .replace("<VIA1>", "")
                    .replace("</VIA1>", "");

            DeviceRgb yellowBg = new DeviceRgb(255, 253, 208);

            String[] lines = mensagem.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    Paragraph p = new Paragraph(line)
                            .setFontSize(10)
                            .setTextAlignment(TextAlignment.LEFT)
                            .setBackgroundColor(yellowBg)
                            .setMarginBottom(2);
                    document.add(p);
                }
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erro ao criar PDF", e);
            throw new FileProcessingException("Erro ao criar documento PDF", e);
        }
    }
}