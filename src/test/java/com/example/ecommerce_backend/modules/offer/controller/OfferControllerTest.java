package com.example.ecommerce_backend.modules.offer.controller;

import com.example.ecommerce_backend.modules.discount.dto.response.DiscountTypeResponse;
import com.example.ecommerce_backend.modules.offer.dto.request.AssignOfferRequest;
import com.example.ecommerce_backend.modules.offer.dto.request.OfferRequest;
import com.example.ecommerce_backend.modules.offer.dto.response.OfferResponse;
import com.example.ecommerce_backend.modules.offer.service.OfferService;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private OfferService offerService;

    private OfferResponse offerResponse;
    private DiscountTypeResponse discountTypeResponse;

    @BeforeEach
    void setUpData() {
        discountTypeResponse = DiscountTypeResponse.builder()
                .id(1L).uuid("dt-uuid").code("PERCENTAGE").name("Percentage")
                .computation("PERCENTAGE").isActive(true).build();

        offerResponse = OfferResponse.builder()
                .uuid("offer-uuid-1").title("Summer Sale")
                .description("Summer sale offer")
                .discountType(discountTypeResponse)
                .discountValue(BigDecimal.TEN)
                .minOrderAmount(BigDecimal.valueOf(50))
                .maxDiscount(BigDecimal.valueOf(25))
                .usageLimit(100).usageLimitPerUser(5)
                .isActive(true).isGlobal(true).totalUsed(0)
                .validFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .validUntil(Instant.now().plus(30, ChronoUnit.DAYS))
                .applicableTo("all")
                .build();
    }

    @Test
    void getAll_shouldReturnOffers() throws Exception {
        when(offerService.getAll(null, null)).thenReturn(List.of(offerResponse));

        mockMvc.perform(get("/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].title").value("Summer Sale"));
    }

    @Test
    void getAll_withFilters_shouldPassParams() throws Exception {
        when(offerService.getAll(eq(true), eq(false))).thenReturn(List.of(offerResponse));

        mockMvc.perform(get("/offers?active=true&global=false"))
                .andExpect(status().isOk());
    }

    @Test
    void getByUuid_shouldReturnOffer() throws Exception {
        when(offerService.getByUuid("offer-uuid-1")).thenReturn(offerResponse);

        mockMvc.perform(get("/offers/offer-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value("Summer Sale"));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        OfferRequest request = new OfferRequest();
        request.setTitle("New Offer");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.TEN);
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(offerService.create(any(OfferRequest.class))).thenReturn(offerResponse);

        mockMvc.perform(post("/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.title").value("Summer Sale"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        OfferRequest request = new OfferRequest();
        request.setTitle("Updated Offer");
        request.setDiscountTypeCode("PERCENTAGE");
        request.setDiscountValue(BigDecimal.valueOf(20));
        request.setValidFrom(Instant.now());
        request.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

        when(offerService.update(eq("offer-uuid-1"), any(OfferRequest.class))).thenReturn(offerResponse);

        mockMvc.perform(put("/offers/offer-uuid-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleStatus_shouldReturnSuccess() throws Exception {
        mockMvc.perform(patch("/offers/offer-uuid-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isActive\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Offer deactivated successfully"));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/offers/offer-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Offer deleted successfully"));
    }

    @Test
    void assignToUsers_shouldReturnSuccess() throws Exception {
        AssignOfferRequest assignRequest = new AssignOfferRequest();
        assignRequest.setUserUuids(List.of("user-uuid-1"));
        assignRequest.setUsageLimitPerUser(5);

        mockMvc.perform(post("/offers/offer-uuid-1/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Offer assigned successfully"));
    }

    @Test
    void removeAssignment_shouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/offers/offer-uuid-1/assign/user-uuid-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Assignment removed successfully"));
    }

    @Test
    void getEligibleOffers_shouldReturnOffers() throws Exception {
        when(offerService.getEligibleOffers(1L)).thenReturn(List.of(offerResponse));

        mockMvc.perform(get("/offers/eligible?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].title").value("Summer Sale"));
    }
}
