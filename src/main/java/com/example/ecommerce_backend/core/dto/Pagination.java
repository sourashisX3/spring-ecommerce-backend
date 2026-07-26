package com.example.ecommerce_backend.core.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination metadata")
public class Pagination {

    @Schema(description = "Current page number (zero-based)", example = "0")
    private int currentPage;

    @Schema(description = "Number of items per page", example = "20")
    private int pageSize;

    @Schema(description = "Total number of elements across all pages", example = "100")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @Schema(description = "Whether a next page exists", example = "true")
    private boolean hasNext;

    @Schema(description = "Whether a previous page exists", example = "false")
    private boolean hasPrevious;

    public static Pagination of(Page<?> page) {
        return Pagination.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}


