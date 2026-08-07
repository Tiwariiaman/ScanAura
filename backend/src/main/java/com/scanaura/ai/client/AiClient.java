package com.scanaura.ai.client;

import com.scanaura.ai.dto.AiMenuResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiClient {

    AiMenuResponse analyzeMenu(
            MultipartFile file
    );

}
