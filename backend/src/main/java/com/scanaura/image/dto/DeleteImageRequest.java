package com.scanaura.image.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteImageRequest {

    @NotBlank
    private String publicId;

}