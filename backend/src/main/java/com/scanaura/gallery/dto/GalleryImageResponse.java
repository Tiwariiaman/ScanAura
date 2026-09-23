package com.scanaura.gallery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImageResponse {

    private UUID id;

    private String imageUrl;

    private Integer displayOrder;
}