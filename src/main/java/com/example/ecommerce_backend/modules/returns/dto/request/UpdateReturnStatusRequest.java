package com.example.ecommerce_backend.modules.returns.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateReturnStatusRequest {
    @NotBlank
    private String status;

    private String resolutionNotes;
}
