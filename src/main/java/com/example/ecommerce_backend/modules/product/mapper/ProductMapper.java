package com.example.ecommerce_backend.modules.product.mapper;

import com.example.ecommerce_backend.modules.product.dto.response.*;
import com.example.ecommerce_backend.modules.product.entity.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        List<VariantResponse> variantResponses = product.getVariants() != null
                ? product.getVariants().stream()
                        .map(ProductMapper::toVariantResponse)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        List<ImageResponse> imageResponses = product.getImages() != null
                ? product.getImages().stream()
                        .map(ProductMapper::toImageResponse)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        String primaryImage = product.getImages() != null
                ? product.getImages().stream()
                        .filter(ProductImage::isPrimary)
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null)
                : null;

        BigDecimal[] priceRange = computePriceRange(product, variantResponses);

        ProductResponse.CategorySummary category = product.getCategory() != null
                ? ProductResponse.CategorySummary.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .slug(product.getCategory().getSlug())
                        .build()
                : null;

        ProductResponse.BrandSummary brand = product.getBrand() != null
                ? ProductResponse.BrandSummary.builder()
                        .id(product.getBrand().getId())
                        .name(product.getBrand().getName())
                        .slug(product.getBrand().getSlug())
                        .logoUrl(product.getBrand().getLogoUrl())
                        .build()
                : null;

        List<ProductResponse.TagSummary> tags = product.getTags() != null
                ? product.getTags().stream()
                        .map(t -> ProductResponse.TagSummary.builder()
                                .id(t.getId())
                                .name(t.getName())
                                .slug(t.getSlug())
                                .build())
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return ProductResponse.builder()
                .uuid(product.getUuid())
                .sku(product.getSku())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .shortDescription(product.getShortDescription())
                .basePrice(product.getBasePrice())
                .minVariantPrice(priceRange[0])
                .maxVariantPrice(priceRange[1])
                .attributes(product.getAttributes())
                .isActive(product.isActive())
                .isFeatured(product.isFeatured())
                .primaryImage(primaryImage)
                .category(category)
                .brand(brand)
                .tags(tags)
                .variants(variantResponses)
                .images(imageResponses)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static VariantResponse toVariantResponse(ProductVariant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .name(variant.getName())
                .price(variant.getPrice())
                .stock(variant.getStock())
                .attributes(variant.getAttributes())
                .isActive(variant.isActive())
                .isDefault(variant.isDefault())
                .selected(false)
                .sortOrder(variant.getSortOrder())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .build();
    }

    public static ImageResponse toImageResponse(ProductImage image) {
        return ImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.isPrimary())
                .sortOrder(image.getSortOrder())
                .build();
    }

    public static void selectVariant(List<VariantResponse> variants, Map<String, String> attributeFilters) {
        if (variants == null || variants.isEmpty()) return;

        VariantResponse selected = null;

        if (attributeFilters != null && !attributeFilters.isEmpty()) {
            selected = variants.stream()
                    .filter(VariantResponse::isActive)
                    .filter(v -> v.getAttributes() != null
                            && attributeFilters.entrySet().stream()
                                    .allMatch(e -> e.getValue().equalsIgnoreCase(
                                            v.getAttributes().get(e.getKey()))))
                    .min(Comparator.comparingInt(VariantResponse::getSortOrder))
                    .orElse(null);
        }

        if (selected == null) {
            selected = variants.stream()
                    .filter(VariantResponse::isDefault)
                    .findFirst()
                    .orElse(null);
        }

        if (selected == null) {
            selected = variants.stream()
                    .filter(VariantResponse::isActive)
                    .min(Comparator.comparingInt(VariantResponse::getSortOrder))
                    .orElse(variants.get(0));
        }

        selected.setSelected(true);
    }

    private static BigDecimal[] computePriceRange(Product product, List<VariantResponse> variants) {
        if (variants.isEmpty()) {
            return new BigDecimal[]{product.getBasePrice(), product.getBasePrice()};
        }

        BigDecimal min = null;
        BigDecimal max = null;

        for (VariantResponse v : variants) {
            BigDecimal effective = v.getPrice() != null ? v.getPrice() : product.getBasePrice();
            if (min == null || effective.compareTo(min) < 0) min = effective;
            if (max == null || effective.compareTo(max) > 0) max = effective;
        }

        return new BigDecimal[]{min, max};
    }
}
