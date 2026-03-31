package com.J2EE.BlogNMCVC.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {
    private String publicId;
    private String secureUrl;
    private String format;
    private Long bytes;
}