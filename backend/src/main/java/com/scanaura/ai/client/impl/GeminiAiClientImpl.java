package com.scanaura.ai.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanaura.ai.client.AiClient;
import com.scanaura.ai.dto.AiMenuResponse;
import com.scanaura.common.constants.AiConstants;
import com.scanaura.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiAiClientImpl implements AiClient {

    private final RestClient geminiRestClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final int MAX_RETRIES = 3;

    @Override
    public AiMenuResponse analyzeMenu(
            MultipartFile file,
            String businessType
    ) {

        try {

            if (file == null || file.isEmpty()) {
                throw new BusinessException("Uploaded file is empty.");
            }

            String base64 = Base64.getEncoder()
                    .encodeToString(file.getBytes());

            Map<String, Object> request = createRequest(
                    base64,
                    file.getContentType(),
                    businessType
            );

            String response = callGeminiWithRetry(request);

            String json = extractJson(response);

            return objectMapper.readValue(
                    json,
                    AiMenuResponse.class
            );

        } catch (BusinessException e) {

            throw e;

        } catch (IOException e) {

            log.error("Failed to read uploaded menu file", e);

            throw new BusinessException(
                    "Unable to read the uploaded file. Please try again."
            );

        } catch (Exception e) {

            log.error("Unexpected error while analyzing menu", e);

            throw new BusinessException(
                    "Unable to analyze the uploaded file. Please try again."
            );
        }
    }

    private String callGeminiWithRetry(
            Map<String, Object> request
    ) {

        int attempt = 0;

        while (attempt < MAX_RETRIES) {

            attempt++;

            try {

                log.info(
                        "Calling Gemini AI. Attempt {}/{}",
                        attempt,
                        MAX_RETRIES
                );

                return geminiRestClient
                        .post()
                        .header("x-goog-api-key", apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(String.class);

            } catch (HttpServerErrorException.ServiceUnavailable e) {

                log.warn(
                        "Gemini returned 503 Service Unavailable. Attempt {}/{}",
                        attempt,
                        MAX_RETRIES
                );

                if (attempt >= MAX_RETRIES) {

                    log.error(
                            "Gemini remained unavailable after {} attempts. Response: {}",
                            MAX_RETRIES,
                            e.getResponseBodyAsString()
                    );

                    throw new BusinessException(
                            "AI service is temporarily unavailable. Please try again in a few seconds."
                    );
                }

                waitBeforeRetry(attempt);

            } catch (HttpClientErrorException.TooManyRequests e) {

                log.warn(
                        "Gemini returned 429 Too Many Requests. Attempt {}/{}",
                        attempt,
                        MAX_RETRIES
                );

                if (attempt >= MAX_RETRIES) {

                    throw new BusinessException(
                            "AI service is temporarily busy. Please try again in a few seconds."
                    );
                }

                waitBeforeRetry(attempt);

            } catch (HttpClientErrorException e) {

                log.error(
                        "Gemini returned client error {}. Response: {}",
                        e.getStatusCode(),
                        e.getResponseBodyAsString()
                );

                throw new BusinessException(
                        "AI could not process the uploaded file. Please check the file and try again."
                );

            } catch (HttpServerErrorException e) {

                log.error(
                        "Gemini returned server error {}. Response: {}",
                        e.getStatusCode(),
                        e.getResponseBodyAsString()
                );

                throw new BusinessException(
                        "AI service is temporarily unavailable. Please try again later."
                );
            }
        }

        throw new BusinessException(
                "AI service is temporarily unavailable. Please try again later."
        );
    }

    private void waitBeforeRetry(int attempt) {

        try {

            long delay = (long) Math.pow(2, attempt - 1) * 1000;

            log.info(
                    "Waiting {} ms before retrying Gemini...",
                    delay
            );

            Thread.sleep(delay);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new BusinessException(
                    "AI request was interrupted. Please try again."
            );
        }
    }

    private Map<String, Object> createRequest(
            String base64,
            String mimeType,
            String businessType
    ) {

        return Map.of(

                "contents",

                List.of(

                        Map.of(

                                "parts",

                                List.of(

                                        Map.of(
                                                "text",
                                                AiConstants.buildMenuAnalysisPrompt(
                                                        businessType
                                                )
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

            log.error(
                    "Gemini response does not contain candidates: {}",
                    response
            );

            throw new BusinessException(
                    "Invalid AI response."
            );
        }

        JsonNode parts = candidates
                .get(0)
                .path("content")
                .path("parts");

        if (!parts.isArray() || parts.isEmpty()) {

            log.error(
                    "Gemini response does not contain content parts: {}",
                    response
            );

            throw new BusinessException(
                    "Invalid AI response."
            );
        }

        String text = parts
                .get(0)
                .path("text")
                .asText();

        if (text == null || text.isBlank()) {

            throw new BusinessException(
                    "AI returned an empty response."
            );
        }

        return cleanJson(text);
    }

    private String cleanJson(String json) {

        return json
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}