package com.example.ecommerce_backend.modules.offer.controller;

import com.example.ecommerce_backend.core.dto.ApiResponse;
import com.example.ecommerce_backend.core.dto.StatusRequest;
import com.example.ecommerce_backend.modules.offer.dto.request.AssignOfferRequest;
import com.example.ecommerce_backend.modules.offer.dto.request.OfferRequest;
import com.example.ecommerce_backend.modules.offer.dto.response.OfferResponse;
import com.example.ecommerce_backend.modules.offer.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
@Tag(name = "Offer", description = "Offer API")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @GetMapping
    @Operation(summary = "Get all offers", description = "Retrieve all offers with optional filters and search")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean global,
            @RequestParam(required = false) String search
    ) {
        List<OfferResponse> offers = offerService.getAll(active, global, search);
        return ApiResponse.success(offers, "Offers retrieved successfully");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get offer by UUID", description = "Retrieve an offer by its UUID")
    public ResponseEntity<ApiResponse<OfferResponse>> getByUuid(@PathVariable String uuid) {
        OfferResponse offer = offerService.getByUuid(uuid);
        return ApiResponse.success(offer, "Offer retrieved successfully");
    }

    @PostMapping
    @Operation(summary = "Create offer", description = "Create a new offer")
    public ResponseEntity<ApiResponse<OfferResponse>> create(@Valid @RequestBody OfferRequest request) {
        OfferResponse offer = offerService.create(request);
        return ApiResponse.created(offer, "Offer created successfully");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update offer", description = "Update an existing offer by UUID")
    public ResponseEntity<ApiResponse<OfferResponse>> update(
            @PathVariable String uuid,
            @Valid @RequestBody OfferRequest request
    ) {
        OfferResponse offer = offerService.update(uuid, request);
        return ApiResponse.success(offer, "Offer updated successfully");
    }

    @PatchMapping("/{uuid}/status")
    @Operation(summary = "Toggle offer status", description = "Activate or deactivate an offer")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String uuid,
            @Valid @RequestBody StatusRequest request
    ) {
        offerService.toggleStatus(uuid, request.isActive());
        String message = request.isActive() ? "Offer activated successfully" : "Offer deactivated successfully";
        return ApiResponse.success(null, message);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete offer", description = "Delete an offer by UUID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String uuid) {
        offerService.delete(uuid);
        return ApiResponse.success(null, "Offer deleted successfully");
    }

    @PostMapping("/{uuid}/assign")
    @Operation(summary = "Assign offer to users", description = "Assign an offer to specific users")
    public ResponseEntity<ApiResponse<Void>> assignToUsers(
            @PathVariable String uuid,
            @Valid @RequestBody AssignOfferRequest request
    ) {
        offerService.assignToUsers(uuid, request.getUserUuids(), request.getUsageLimitPerUser());
        return ApiResponse.success(null, "Offer assigned successfully");
    }

    @DeleteMapping("/{uuid}/assign/{userUuid}")
    @Operation(summary = "Remove offer assignment", description = "Remove an offer assignment from a user")
    public ResponseEntity<ApiResponse<Void>> removeAssignment(
            @PathVariable String uuid,
            @PathVariable String userUuid
    ) {
        offerService.removeAssignment(uuid, userUuid);
        return ApiResponse.success(null, "Assignment removed successfully");
    }

    @GetMapping("/eligible")
    @Operation(summary = "Get eligible offers", description = "Retrieve offers eligible for a specific user")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getEligibleOffers(
            @RequestParam Long userId
    ) {
        List<OfferResponse> offers = offerService.getEligibleOffers(userId);
        return ApiResponse.success(offers, "Eligible offers retrieved successfully");
    }
}
