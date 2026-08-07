package com.scanaura.ai.service;

import com.scanaura.ai.dto.AiImportRequest;
import com.scanaura.ai.dto.AiMenuResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {

    AiMenuResponse analyzeMenu(MultipartFile file);

    void importMenu(AiImportRequest request);

}