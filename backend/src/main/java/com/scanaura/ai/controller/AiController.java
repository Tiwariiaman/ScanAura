package com.scanaura.ai.controller;

import com.scanaura.ai.dto.AiImportRequest;
import com.scanaura.ai.dto.AiMenuResponse;
import com.scanaura.ai.service.AiService;
import com.scanaura.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ai/menu")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<AiMenuResponse>> analyzeMenu(
            @RequestParam MultipartFile file
    ) {

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Menu analyzed successfully.",

                        aiService.analyzeMenu(file)

                )

        );

    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importMenu(

            @Valid
            @RequestBody
            AiImportRequest request

    ) {

        aiService.importMenu(request);

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Menu imported successfully.",

                        "Success"

                )

        );

    }

}