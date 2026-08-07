package com.scanaura.ai.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanaura.ai.client.AiClient;
import com.scanaura.ai.dto.AiMenuResponse;
import com.scanaura.common.constants.AiConstants;
import com.scanaura.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiAiClientImpl implements AiClient {

    private final RestClient geminiRestClient;

    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Override
    public AiMenuResponse analyzeMenu(MultipartFile file) {

        try {

            String base64 = Base64.getEncoder()
                    .encodeToString(file.getBytes());

            Map<String, Object> request = createRequest(
                    base64,
                    file.getContentType()
            );

            String response = geminiRestClient
                    .post()
                    .uri("?key={key}", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            String json = extractJson(response);

//            System.out.println("========== Gemini Response ==========");
//            System.out.println(json);
//            System.out.println("=====================================");

            return objectMapper.readValue(
                    json,
                    AiMenuResponse.class
            );

        }catch (Exception e) {

            e.printStackTrace();   // Temporary for debugging

            throw new BusinessException(
                    e.getMessage()
            );

        }

    }

    private Map<String, Object> createRequest(
            String base64,
            String mimeType
    ) {

        return Map.of(

                "contents",

                List.of(

                        Map.of(

                                "parts",

                                List.of(

                                        Map.of(
                                                "text",
                                                AiConstants.MENU_ANALYSIS_PROMPT
                                        ),

                                        Map.of(

                                                "inline_data",

                                                Map.of(

                                                        "mime_type",
                                                        mimeType,

                                                        "data",
                                                        base64

                                                )

                                        )

                                )

                        )

                )

        );

    }

    private String extractJson(String response)
            throws IOException {

        JsonNode root = objectMapper.readTree(response);

        JsonNode candidates = root.path("candidates");

        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new BusinessException("Invalid AI response.");
        }

        JsonNode parts = candidates.get(0)
                .path("content")
                .path("parts");

        if (!parts.isArray() || parts.isEmpty()) {
            throw new BusinessException("Invalid AI response.");
        }

        String text = parts.get(0)
                .path("text")
                .asText();

        return cleanJson(text);

    }

    private String cleanJson(String json) {

        return json
                .replace("```json", "")
                .replace("```", "")
                .trim();

    }

}