package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.brand.entity.Brand;
import com.example.ecommerce_backend.modules.brand.exception.BrandNotFoundException;
import com.example.ecommerce_backend.modules.brand.repository.BrandRepository;
import com.example.ecommerce_backend.modules.category.entity.Category;
import com.example.ecommerce_backend.modules.category.exception.CategoryNotFoundException;
import com.example.ecommerce_backend.modules.category.repository.CategoryRepository;
import com.example.ecommerce_backend.modules.category.service.CategoryService;
import com.example.ecommerce_backend.modules.image.dto.request.ImageRequest;
import com.example.ecommerce_backend.modules.image.entity.ProductImage;
import com.example.ecommerce_backend.modules.image.repository.ProductImageRepository;
import com.example.ecommerce_backend.modules.product.dto.request.ProductRequest;
import com.example.ecommerce_backend.modules.product.dto.response.ProductResponse;
import com.example.ecommerce_backend.modules.product.entity.Product;
import com.example.ecommerce_backend.modules.product.exception.DuplicateSkuException;
import com.example.ecommerce_backend.modules.product.exception.ProductNotFoundException;
import com.example.ecommerce_backend.modules.product.mapper.ProductMapper;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import com.example.ecommerce_backend.modules.product.specification.ProductSpecification;
import com.example.ecommerce_backend.modules.review.repository.ReviewRepository;
import com.example.ecommerce_backend.modules.tag.entity.Tag;
import com.example.ecommerce_backend.modules.tag.exception.TagNotFoundException;
import com.example.ecommerce_backend.modules.tag.repository.TagRepository;
import com.example.ecommerce_backend.modules.variant.dto.request.VariantRequest;
import com.example.ecommerce_backend.modules.variant.entity.ProductVariant;
import com.example.ecommerce_backend.modules.variant.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductImageRepository imageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(
            List<String> categorySlugs,
            List<String> brandSlugs,
            List<String> tagSlugs,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isFeatured,
            Boolean active,
            Map<String, String> attributeFilters,
            Pageable pageable
    ) {
        Set<Long> categoryIds = resolveCategoryIds(categorySlugs);

        Specification<Product> spec = ProductSpecification.withFilters(
                categoryIds, brandSlugs, tagSlugs, search, minPrice, maxPrice, isFeatured, active
        );

        Page<Product> products = productRepository.findAll(spec, pageable);

        Page<ProductResponse> responsePage;
        if (attributeFilters != null && !attributeFilters.isEmpty()) {
            List<Product> filtered = products.getContent().stream()
                    .filter(p -> matchesAttributes(p.getAttributes(), attributeFilters))
                    .collect(Collectors.toList());
            responsePage = new org.springframework.data.domain.PageImpl<>(
                    filtered.stream().map(ProductMapper::toResponse).collect(Collectors.toList()),
                    pageable,
                    filtered.size()
            );
        } else {
            responsePage = products.map(ProductMapper::toResponse);
        }

        responsePage.getContent().forEach(p -> {
            if (p.getVariants() != null) {
                ProductMapper.selectVariant(p.getVariants(), attributeFilters);
            }
        });

        return responsePage;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(
            List<String> categorySlugs,
            List<String> brandSlugs,
            List<String> tagSlugs,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isFeatured,
            Boolean active,
            Map<String, String> attributeFilters,
            Sort sort
    ) {
        Set<Long> categoryIds = resolveCategoryIds(categorySlugs);

        Specification<Product> spec = ProductSpecification.withFilters(
                categoryIds, brandSlugs, tagSlugs, search, minPrice, maxPrice, isFeatured, active
        );

        List<Product> products = productRepository.findAll(spec, sort);

        List<ProductResponse> responseList;
        if (attributeFilters != null && !attributeFilters.isEmpty()) {
            responseList = products.stream()
                    .filter(p -> matchesAttributes(p.getAttributes(), attributeFilters))
                    .map(ProductMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            responseList = products.stream()
                    .map(ProductMapper::toResponse)
                    .collect(Collectors.toList());
        }

        responseList.forEach(p -> {
            if (p.getVariants() != null) {
                ProductMapper.selectVariant(p.getVariants(), attributeFilters);
            }
        });

        return responseList;
    }

    private Set<Long> resolveCategoryIds(List<String> categorySlugs) {
        if (categorySlugs == null || categorySlugs.isEmpty()) {
            return null;
        }
        Set<Long> ids = new HashSet<>();
        for (String slug : categorySlugs) {
            if (slug != null && !slug.isBlank()) {
                ids.addAll(categoryService.getDescendantCategoryIds(slug));
            }
        }
        return ids;
    }

    @Transactional(readOnly = true)
    public ProductResponse getByUuid(String uuid, Map<String, String> attributeFilters) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ProductNotFoundException(uuid));
        ProductResponse response = ProductMapper.toResponse(product);
        if (response.getVariants() != null) {
            ProductMapper.selectVariant(response.getVariants(), attributeFilters);
        }
        populateReviews(response, product.getId());
        return response;
    }

    @Transactional(readOnly = true)
    public void populateReviewStats(List<ProductResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return;
        }
        List<String> uuids = responses.stream()
                .map(ProductResponse::getUuid)
                .filter(Objects::nonNull)
                .toList();
        if (uuids.isEmpty()) {
            return;
        }
        Map<String, Object[]> statsByUuid = reviewRepository.getReviewStatsByProductUuids(uuids).stream()
                .collect(Collectors.toMap(row -> (String) row[0], row -> row));
        for (ProductResponse response : responses) {
            Object[] row = statsByUuid.get(response.getUuid());
            if (row == null) {
                continue;
            }
            double avg = ((Number) row[1]).doubleValue();
            long count = ((Number) row[2]).longValue();
            response.setReviewStats(ProductResponse.ReviewStats.builder()
                    .averageRating(BigDecimal.valueOf(avg)
                            .setScale(2, java.math.RoundingMode.HALF_UP)
                            .doubleValue())
                    .totalCount((int) count)
                    .build());
        }
    }

    private void populateReviews(ProductResponse response, Long productId) {
        double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        long totalCount = reviewRepository.getReviewCountByProductId(productId);

        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }
        List<Object[]> rows = reviewRepository.getRatingDistributionByProductId(productId);
        for (Object[] row : rows) {
            distribution.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }

        response.setReviewStats(ProductResponse.ReviewStats.builder()
                .averageRating(BigDecimal.valueOf(avgRating)
                        .setScale(2, java.math.RoundingMode.HALF_UP)
                        .doubleValue())
                .totalCount((int) totalCount)
                .ratingDistribution(distribution)
                .build());

        List<ProductResponse.ReviewSummary> summaries = reviewRepository
                .findTop5ByProductIdAndIsActiveTrueOrderByCreatedAtDesc(productId)
                .stream()
                .map(r -> ProductResponse.ReviewSummary.builder()
                        .uuid(r.getUuid())
                        .rating(r.getRating())
                        .title(r.getTitle())
                        .comment(r.getComment())
                        .userName(r.getUser().getUsername())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        response.setRecentReviews(summaries);
    }

    @Transactional
    @RequiresPermission("product:write")
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        Category category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategorySlug()));

        Brand brand = brandRepository.findBySlug(request.getBrandSlug())
                .orElseThrow(() -> new BrandNotFoundException(request.getBrandSlug()));

        Set<Tag> tags = new HashSet<>();
        if (request.getTagSlugs() != null) {
            for (String tagSlug : request.getTagSlugs()) {
                tags.add(tagRepository.findBySlug(tagSlug)
                        .orElseThrow(() -> new TagNotFoundException(tagSlug)));
            }
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .slug(generateUniqueSlug(request.getName()))
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .basePrice(request.getBasePrice())
                .attributes(request.getAttributes() != null ? request.getAttributes() : new HashMap<>())
                .category(category)
                .brand(brand)
                .tags(tags)
                .isFeatured(request.isFeatured())
                .build();

        if (request.getVariants() != null) {
            boolean hasDefault = false;
            for (VariantRequest vr : request.getVariants()) {
                if (variantRepository.existsBySku(vr.getSku())) {
                    throw new DuplicateSkuException(vr.getSku());
                }
                if (vr.isDefault()) {
                    if (hasDefault) {
                        vr.setDefault(false);
                    } else {
                        hasDefault = true;
                    }
                }
                ProductVariant variant = ProductVariant.builder()
                        .sku(vr.getSku())
                        .name(vr.getName())
                        .price(vr.getPrice())
                        .stock(vr.getStock())
                        .attributes(vr.getAttributes() != null ? vr.getAttributes() : new HashMap<>())
                        .isDefault(vr.isDefault())
                        .sortOrder(vr.getSortOrder())
                        .product(product)
                        .build();
                product.getVariants().add(variant);
            }
        }

        if (request.getImages() != null) {
            for (ImageRequest ir : request.getImages()) {
                ProductImage image = ProductImage.builder()
                        .imageUrl(ir.getImageUrl())
                        .isPrimary(ir.isPrimary())
                        .sortOrder(ir.getSortOrder())
                        .product(product)
                        .build();
                product.getImages().add(image);
            }
        }

        product = productRepository.save(product);
        return ProductMapper.toResponse(product);
    }

    @Transactional
    @RequiresPermission("product:write")
    public ProductResponse update(String uuid, ProductRequest request) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ProductNotFoundException(uuid));

        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        Category category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategorySlug()));

        Brand brand = brandRepository.findBySlug(request.getBrandSlug())
                .orElseThrow(() -> new BrandNotFoundException(request.getBrandSlug()));

        Set<Tag> tags = new HashSet<>();
        if (request.getTagSlugs() != null) {
            for (String tagSlug : request.getTagSlugs()) {
                tags.add(tagRepository.findBySlug(tagSlug)
                        .orElseThrow(() -> new TagNotFoundException(tagSlug)));
            }
        }

        product.setSku(request.getSku());
        product.setName(request.getName());
        if (!product.getSlug().equals(generateSlug(request.getName()))) {
            product.setSlug(generateUniqueSlug(request.getName()));
        }
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setBasePrice(request.getBasePrice());
        product.setAttributes(request.getAttributes() != null ? request.getAttributes() : new HashMap<>());
        product.setCategory(category);
        product.setBrand(brand);
        product.setTags(tags);
        product.setFeatured(request.isFeatured());

        product.getVariants().clear();
        if (request.getVariants() != null) {
            boolean hasDefault = false;
            for (VariantRequest vr : request.getVariants()) {
                if (variantRepository.existsBySku(vr.getSku())) {
                    boolean isOwnVariant = product.getVariants().stream()
                            .anyMatch(v -> v.getSku().equals(vr.getSku()));
                    if (!isOwnVariant) {
                        throw new DuplicateSkuException(vr.getSku());
                    }
                }
                if (vr.isDefault()) {
                    if (hasDefault) {
                        vr.setDefault(false);
                    } else {
                        hasDefault = true;
                    }
                }
                ProductVariant variant = ProductVariant.builder()
                        .sku(vr.getSku())
                        .name(vr.getName())
                        .price(vr.getPrice())
                        .stock(vr.getStock())
                        .attributes(vr.getAttributes() != null ? vr.getAttributes() : new HashMap<>())
                        .isDefault(vr.isDefault())
                        .sortOrder(vr.getSortOrder())
                        .product(product)
                        .build();
                product.getVariants().add(variant);
            }
        }

        product.getImages().clear();
        if (request.getImages() != null) {
            for (ImageRequest ir : request.getImages()) {
                ProductImage image = ProductImage.builder()
                        .imageUrl(ir.getImageUrl())
                        .isPrimary(ir.isPrimary())
                        .sortOrder(ir.getSortOrder())
                        .product(product)
                        .build();
                product.getImages().add(image);
            }
        }

        product = productRepository.save(product);
        return ProductMapper.toResponse(product);
    }

    @Transactional
    @RequiresPermission("product:write")
    public void delete(String uuid) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ProductNotFoundException(uuid));
        productRepository.delete(product);
    }

    @Transactional
    @RequiresPermission("product:write")
    public boolean toggleStatus(String uuid, boolean isActive) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ProductNotFoundException(uuid));
        if (product.isActive() == isActive) {
            return false;
        }
        product.setActive(isActive);
        productRepository.save(product);
        return true;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getSimilarProducts(String uuid, int limit) {
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ProductNotFoundException(uuid));

        List<Product> candidates = productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());

        List<SimilarProduct> scored = new ArrayList<>();
        for (Product candidate : candidates) {
            if (candidate.getId().equals(product.getId())) continue;

            int score = 0;
            if (candidate.getBrand() != null && product.getBrand() != null
                    && candidate.getBrand().getId().equals(product.getBrand().getId())) {
                score += 3;
            }

            if (candidate.getTags() != null && product.getTags() != null) {
                Set<Long> productTagIds = product.getTags().stream()
                        .map(Tag::getId).collect(Collectors.toSet());
                for (Tag tag : candidate.getTags()) {
                    if (productTagIds.contains(tag.getId())) {
                        score += 1;
                    }
                }
            }

            if (candidate.getAttributes() != null && product.getAttributes() != null) {
                for (Map.Entry<String, String> entry : product.getAttributes().entrySet()) {
                    String candidateValue = candidate.getAttributes().get(entry.getKey());
                    if (candidateValue != null && candidateValue.equals(entry.getValue())) {
                        score += 2;
                    }
                }
            }

            if (score > 0) {
                scored.add(new SimilarProduct(candidate, score));
            }
        }

        scored.sort((a, b) -> Integer.compare(b.score, a.score));

        return scored.stream()
                .limit(limit)
                .map(sp -> ProductMapper.toResponse(sp.product))
                .collect(Collectors.toList());
    }

    private boolean matchesAttributes(Map<String, String> productAttrs, Map<String, String> filters) {
        if (filters == null || filters.isEmpty()) return true;
        if (productAttrs == null) return false;
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            String value = productAttrs.get(filter.getKey());
            if (value == null || !value.equalsIgnoreCase(filter.getValue())) {
                return false;
            }
        }
        return true;
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = generateSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (productRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        return slug;
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private record SimilarProduct(Product product, int score) {}
}
