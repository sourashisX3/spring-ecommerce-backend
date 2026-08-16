package com.example.ecommerce_backend.modules.banner.repository;

import com.example.ecommerce_backend.modules.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Optional<Banner> findByUuid(String uuid);

    List<Banner> findAllByOrderBySortOrderAsc();

    List<Banner> findByIsActiveTrueOrderBySortOrderAsc();

    List<Banner> findByIsActiveOrderBySortOrderAsc(boolean isActive);
}