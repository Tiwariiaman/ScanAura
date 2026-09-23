package com.scanaura.gallery.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GalleryReorderRequest {

    @NotEmpty(message = "Image IDs are required.")
    private List<UUID> imageIds;
}