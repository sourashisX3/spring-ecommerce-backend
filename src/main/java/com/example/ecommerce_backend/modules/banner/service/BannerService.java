package com.example.ecommerce_backend.modules.banner.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.banner.dto.request.BannerRequest;
import com.example.ecommerce_backend.modules.banner.dto.response.BannerResponse;
import com.example.ecommerce_backend.modules.banner.entity.Banner;
import com.example.ecommerce_backend.modules.banner.exception.BannerNotFoundException;
import com.example.ecommerce_backend.modules.banner.mapper.BannerMapper;
import com.example.ecommerce_backend.modules.banner.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Transactional(readOnly = true)
    public List<BannerResponse> getAll(Boolean active) {
        List<Banner> banners;
        if (active != null) {
            banners = bannerRepository.findByIsActiveOrderBySortOrderAsc(active);
        } else {
            banners = bannerRepository.findAllByOrderBySortOrderAsc();
        }
        return banners.stream()
                .map(BannerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BannerResponse> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(BannerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BannerResponse getByUuid(String uuid) {
        Banner banner = bannerRepository.findByUuid(uuid)
                .orElseThrow(() -> new BannerNotFoundException(uuid));
        return BannerMapper.toResponse(banner);
    }

    @Transactional
    @RequiresPermission("banner:write")
    public BannerResponse create(BannerRequest request) {
        Banner banner = Banner.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .imageUrl(request.getImageUrl())
                .linkType(request.getLinkType())
                .linkValue(request.getLinkValue())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .build();

        banner = bannerRepository.save(banner);
        return BannerMapper.toResponse(banner);
    }

    @Transactional
    @RequiresPermission("banner:write")
    public BannerResponse update(String uuid, BannerRequest request) {
        Banner banner = bannerRepository.findByUuid(uuid)
                .orElseThrow(() -> new BannerNotFoundException(uuid));

        banner.setTitle(request.getTitle());
        banner.setSubtitle(request.getSubtitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkType(request.getLinkType());
        banner.setLinkValue(request.getLinkValue());
        if (request.getSortOrder() != null) {
            banner.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            banner.setActive(request.getIsActive());
        }
        banner.setValidFrom(request.getValidFrom());
        banner.setValidUntil(request.getValidUntil());

        banner = bannerRepository.save(banner);
        return BannerMapper.toResponse(banner);
    }

    @Transactional
    @RequiresPermission("banner:write")
    public void toggleStatus(String uuid, boolean active) {
        Banner banner = bannerRepository.findByUuid(uuid)
                .orElseThrow(() -> new BannerNotFoundException(uuid));
        banner.setActive(active);
        bannerRepository.save(banner);
    }

    @Transactional
    @RequiresPermission("banner:write")
    public void delete(String uuid) {
        Banner banner = bannerRepository.findByUuid(uuid)
                .orElseThrow(() -> new BannerNotFoundException(uuid));
        bannerRepository.delete(banner);
    }
}