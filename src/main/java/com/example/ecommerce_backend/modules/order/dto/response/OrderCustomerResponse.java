package com.example.ecommerce_backend.modules.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order customer summary")
public class OrderCustomerResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "User UUID")
    private String uuid;
    @Schema(description = "First name")
    private String firstName;
    @Schema(description = "Last name")
    private String lastName;
    @Schema(description = "Email", example = "user@example.com")
    private String email;
    @Schema(description = "Dial code", example = "+91")
    private String dialCode;
    @Schema(description = "Phone number")
    private String phoneNumber;
}
