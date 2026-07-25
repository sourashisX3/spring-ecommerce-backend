package com.example.ecommerce_backend.modules.image.service;

import com.example.ecommerce_backend.modules.image.dto.request.ImageRequest;
import com.example.ecommerce_backend.modules.image.dto.response.ImageResponse;
import com.example.ecommerce_backend.modules.image.entity.ProductImage;
import com.example.ecommerce_backend.modules.image.exception.ImageNotFoundException;
import com.example.ecommerce_backend.modules.image.repository.ProductImageRepository;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ProductImageRepository imageRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ImageService imageService;

    @Test
    void addImage_shouldAddNonPrimaryImage() {
        Product product = Product.builder().id(1L).uuid("product-uuid").images(new ArrayList<>()).build();
        ImageRequest request = new ImageRequest();
        request.setImageUrl("http://example.com/img.jpg");
        request.setPrimary(false);
        request.setSortOrder(1);

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));

        ProductImage saved = ProductImage.builder()
                .id(1L).uuid("img-uuid").imageUrl("http://example.com/img.jpg")
                .isPrimary(false).sortOrder(1).product(product).build();
        when(imageRepository.save(any(ProductImage.class))).thenReturn(saved);

        ImageResponse result = imageService.addImage("product-uuid", request);

        assertThat(result.getUuid()).isEqualTo("img-uuid");
        assertThat(result.getImageUrl()).isEqualTo("http://example.com/img.jpg");
        assertThat(result.isPrimary()).isFalse();
        verify(imageRepository).save(any(ProductImage.class));
    }

    @Test
    void addImage_withPrimary_shouldUnmarkExistingPrimaries() {
        ProductImage existingPrimary = ProductImage.builder().id(1L).isPrimary(true).sortOrder(0).build();
        ProductImage existingNonPrimary = ProductImage.builder().id(2L).isPrimary(false).sortOrder(1).build();
        List<ProductImage> images = new ArrayList<>(List.of(existingPrimary, existingNonPrimary));
        Product product = Product.builder().id(1L).uuid("product-uuid").images(images).build();

        ImageRequest request = new ImageRequest();
        request.setImageUrl("http://example.com/new.jpg");
        request.setPrimary(true);
        request.setSortOrder(2);

        when(productRepository.findByUuid("product-uuid")).thenReturn(Optional.of(product));

        ProductImage saved = ProductImage.builder()
                .id(3L).uuid("new-img-uuid").imageUrl("http://example.com/new.jpg")
                .isPrimary(true).sortOrder(2).product(product).build();
        when(imageRepository.save(any(ProductImage.class))).thenReturn(saved);

        imageService.addImage("product-uuid", request);

        assertThat(existingPrimary.isPrimary()).isFalse();
        assertThat(existingNonPrimary.isPrimary()).isFalse();
    }

    @Test
    void addImage_whenProductNotFound_shouldThrow() {
        when(productRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        ImageRequest request = new ImageRequest();
        request.setImageUrl("http://example.com/img.jpg");

        assertThatThrownBy(() -> imageService.addImage("nonexistent", request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void deleteImage_shouldDeleteNonPrimaryImage() {
        Product product = Product.builder().id(1L).build();
        ProductImage image = ProductImage.builder()
                .id(1L).uuid("img-uuid").isPrimary(false).product(product).build();
        when(imageRepository.findByUuid("img-uuid")).thenReturn(Optional.of(image));

        imageService.deleteImage("img-uuid");

        verify(imageRepository).delete(image);
        verify(imageRepository, never()).findByProductId(anyLong());
    }

    @Test
    void deleteImage_whenPrimaryWithRemaining_shouldReassignPrimary() {
        Product product = Product.builder().id(1L).build();
        ProductImage image = ProductImage.builder()
                .id(1L).uuid("img-uuid").isPrimary(true).sortOrder(0).product(product).build();
        ProductImage remaining1 = ProductImage.builder()
                .id(2L).uuid("other-1").isPrimary(false).sortOrder(1).product(product).build();
        ProductImage remaining2 = ProductImage.builder()
                .id(3L).uuid("other-2").isPrimary(false).sortOrder(2).product(product).build();

        when(imageRepository.findByUuid("img-uuid")).thenReturn(Optional.of(image));
        when(imageRepository.findByProductId(1L)).thenReturn(List.of(remaining1, remaining2));

        imageService.deleteImage("img-uuid");

        verify(imageRepository).delete(image);
        verify(imageRepository).findByProductId(1L);
        assertThat(remaining1.isPrimary()).isTrue();
        assertThat(remaining2.isPrimary()).isFalse();
        verify(imageRepository).save(remaining1);
    }

    @Test
    void deleteImage_whenPrimaryWithNoRemaining_shouldJustDelete() {
        Product product = Product.builder().id(1L).build();
        ProductImage image = ProductImage.builder()
                .id(1L).uuid("img-uuid").isPrimary(true).product(product).build();

        when(imageRepository.findByUuid("img-uuid")).thenReturn(Optional.of(image));
        when(imageRepository.findByProductId(1L)).thenReturn(List.of());

        imageService.deleteImage("img-uuid");

        verify(imageRepository).delete(image);
        verify(imageRepository).findByProductId(1L);
        verify(imageRepository, never()).save(any());
    }

    @Test
    void deleteImage_whenNotFound_shouldThrow() {
        when(imageRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.deleteImage("nonexistent"))
                .isInstanceOf(ImageNotFoundException.class);
    }
}
