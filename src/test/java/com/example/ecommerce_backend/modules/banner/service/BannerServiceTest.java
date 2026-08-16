package com.example.ecommerce_backend.modules.banner.service;

import com.example.ecommerce_backend.modules.banner.dto.request.BannerRequest;
import com.example.ecommerce_backend.modules.banner.dto.response.BannerResponse;
import com.example.ecommerce_backend.modules.banner.entity.Banner;
import com.example.ecommerce_backend.modules.banner.exception.BannerNotFoundException;
import com.example.ecommerce_backend.modules.banner.repository.BannerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

    @Mock
    private BannerRepository bannerRepository;

    @InjectMocks
    private BannerService bannerService;

    private Banner banner;
    private Banner inactiveBanner;

    @BeforeEach
    void setUp() {
        banner = Banner.builder()
                .id(1L).uuid("banner-uuid-1").title("Summer Sale")
                .subtitle("Up to 40% off").imageUrl("/images/banner1.jpg")
                .linkType("PRODUCT").linkValue("product-uuid-1")
                .sortOrder(1).isActive(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();

        inactiveBanner = Banner.builder()
                .id(2L).uuid("banner-uuid-2").title("Old Campaign")
                .imageUrl("/images/banner2.jpg").linkType("URL")
                .linkValue("https://example.com").sortOrder(2).isActive(false)
                .build();
    }

    // --- getAll ---

    @Test
    void getAll_withNoActiveFilter_shouldReturnAllOrderedBySortOrder() {
        when(bannerRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(banner, inactiveBanner));

        List<BannerResponse> result = bannerService.getAll(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Summer Sale");
    }

    @Test
    void getAll_withActiveTrue_shouldReturnActiveOnly() {
        when(bannerRepository.findByIsActiveOrderBySortOrderAsc(true)).thenReturn(List.of(banner));

        List<BannerResponse> result = bannerService.getAll(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Summer Sale");
    }

    @Test
    void getAll_withActiveFalse_shouldReturnInactiveOnly() {
        when(bannerRepository.findByIsActiveOrderBySortOrderAsc(false)).thenReturn(List.of(inactiveBanner));

        List<BannerResponse> result = bannerService.getAll(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Old Campaign");
    }

    // --- getActiveBanners ---

    @Test
    void getActiveBanners_shouldReturnActiveSortedBanners() {
        when(bannerRepository.findByIsActiveTrueOrderBySortOrderAsc()).thenReturn(List.of(banner));

        List<BannerResponse> result = bannerService.getActiveBanners();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageUrl()).isEqualTo("/images/banner1.jpg");
    }

    // --- getByUuid ---

    @Test
    void getByUuid_shouldReturnBanner() {
        when(bannerRepository.findByUuid("banner-uuid-1")).thenReturn(Optional.of(banner));

        BannerResponse result = bannerService.getByUuid("banner-uuid-1");

        assertThat(result.getTitle()).isEqualTo("Summer Sale");
        assertThat(result.getLinkType()).isEqualTo("PRODUCT");
    }

    @Test
    void getByUuid_whenNotFound_shouldThrow() {
        when(bannerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerService.getByUuid("nonexistent"))
                .isInstanceOf(BannerNotFoundException.class);
    }

    // --- create ---

    @Test
    void create_shouldSaveAndReturnBanner() {
        BannerRequest request = new BannerRequest();
        request.setTitle("New Banner");
        request.setSubtitle("Fresh campaign");
        request.setImageUrl("/images/new.jpg");
        request.setLinkType("CATEGORY");
        request.setLinkValue("whisky");
        request.setSortOrder(3);

        when(bannerRepository.save(any(Banner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BannerResponse result = bannerService.create(request);

        assertThat(result.getTitle()).isEqualTo("New Banner");
        assertThat(result.getLinkType()).isEqualTo("CATEGORY");
        assertThat(result.getLinkValue()).isEqualTo("whisky");
        assertThat(result.getSortOrder()).isEqualTo(3);
        assertThat(result.isActive()).isTrue();
        verify(bannerRepository).save(any(Banner.class));
    }

    // --- update ---

    @Test
    void update_shouldUpdateAndReturnBanner() {
        BannerRequest request = new BannerRequest();
        request.setTitle("Updated Banner");
        request.setImageUrl("/images/updated.jpg");
        request.setLinkType("URL");
        request.setLinkValue("https://example.com");
        request.setSortOrder(5);
        request.setIsActive(false);

        when(bannerRepository.findByUuid("banner-uuid-1")).thenReturn(Optional.of(banner));
        when(bannerRepository.save(any(Banner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BannerResponse result = bannerService.update("banner-uuid-1", request);

        assertThat(result.getTitle()).isEqualTo("Updated Banner");
        assertThat(result.getLinkType()).isEqualTo("URL");
        assertThat(result.getSortOrder()).isEqualTo(5);
        assertThat(result.isActive()).isFalse();
    }

    @Test
    void update_whenNotFound_shouldThrow() {
        when(bannerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerService.update("nonexistent", new BannerRequest()))
                .isInstanceOf(BannerNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_shouldToggle() {
        when(bannerRepository.findByUuid("banner-uuid-1")).thenReturn(Optional.of(banner));

        bannerService.toggleStatus("banner-uuid-1", false);

        assertThat(banner.isActive()).isFalse();
        verify(bannerRepository).save(banner);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(bannerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerService.toggleStatus("nonexistent", true))
                .isInstanceOf(BannerNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteBanner() {
        when(bannerRepository.findByUuid("banner-uuid-1")).thenReturn(Optional.of(banner));

        bannerService.delete("banner-uuid-1");

        verify(bannerRepository).delete(banner);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(bannerRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bannerService.delete("nonexistent"))
                .isInstanceOf(BannerNotFoundException.class);
    }
}