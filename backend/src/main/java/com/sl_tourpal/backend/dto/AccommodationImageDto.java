package com.sl_tourpal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImageDto {
    private String preview;  // base64 image data or URL
    private String hint;     // image description/hint
    // Note: File object handling would be in multipart requests
}
