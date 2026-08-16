package com.example.ecommerce_backend.modules.banner.controller;

import com.example.ecommerce_backend.modules.banner.dto.request.BannerRequest;
import com.example.ecommerce_backend.modules.banner.dto.response.BannerResponse;
import com.example.ecommerce_backend.modules.banner.service.BannerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private BannerService bannerService;

    private BannerResponse bannerResponse;

    @BeforeEach
    void setUpData() {
        bannerResponse = BannerResponse.builder()
                .uuid("banner-uuid-1").title("Summer Sale")
                .subtitle("Up to 40% off").imageUrl("/images/banner1.jpg")
                .linkType("PRODUCT").linkValue("product-uuid-1")
                .sortOrder(1).isActive(true)
                .build();
    }

    @Test
    void getAll_shouldReturnBanners() throws Exception {
        when(bannerService.getAll(isNull())).thenReturn(List.of(bannerResponse));

        mockMvc.perform(get("/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].title").value("Summer Sale"));
    }

    @Test
    void getAll_withActiveFilter_shouldPassParam() throws Exception {
        when(bannerService.getAll(eq(true))).thenReturn(List.of(bannerResponse));

        mockMvc.perform(get("/banners?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].linkType").value("PRODUCT"));
    }

    @Test
    void getByUuid_shouldReturnBanner() throws Exception {
        when(bannerService.getByUuid("banner-uuid-1")).thenReturn(bannerResponse);

        mockMvc.perform(get("/banners/banner-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value("Summer Sale"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        BannerRequest request = new BannerRequest();
        request.setTitle("New Banner");
        request.setImageUrl("/images/new.jpg");
        request.setLinkType("CATEGORY");
        request.setLinkValue("whisky");

        when(bannerService.create(any(BannerRequest.class))).thenReturn(bannerResponse);

        mockMvc.perform(post("/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.title").value("Summer Sale"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        BannerRequest request = new BannerRequest();
        request.setTitle("Updated Banner");
        request.setImageUrl("/images/updated.jpg");
        request.setLinkType("URL");

        when(bannerService.update(eq("banner-uuid-1"), any(BannerRequest.class))).thenReturn(bannerResponse);

        mockMvc.perform(put("/banners/banner-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        mockMvc.perform(patch("/banners/banner-uuid-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Banner deactivated successfully"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/banners/banner-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Banner deleted successfully"));
    }
}