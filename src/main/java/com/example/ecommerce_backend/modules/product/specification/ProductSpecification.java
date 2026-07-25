package com.example.ecommerce_backend.modules.product.specification;

import com.example.ecommerce_backend.modules.product.entity.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> withFilters(
            Set<Long> categoryIds,
            String brandSlug,
            String tagSlug,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isFeatured,
            Boolean active
    ) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryIds != null && !categoryIds.isEmpty()) {
                Join<Object, Object> categoryJoin = root.join("category");
                predicates.add(categoryJoin.get("id").in(categoryIds));
            } else if (categoryIds != null) {
                predicates.add(cb.disjunction());
            }

            if (brandSlug != null && !brandSlug.isBlank()) {
                Join<Object, Object> brandJoin = root.join("brand");
                predicates.add(cb.equal(brandJoin.get("slug"), brandSlug));
            }

            if (tagSlug != null && !tagSlug.isBlank()) {
                Join<Object, Object> tagJoin = root.join("tags");
                predicates.add(cb.equal(tagJoin.get("slug"), tagSlug));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("slug")), pattern),
                        cb.like(cb.lower(root.get("shortDescription")), pattern)
                ));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice));
            }

            if (isFeatured != null) {
                predicates.add(cb.equal(root.get("isFeatured"), isFeatured));
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("isActive"), active));
            } else {
                predicates.add(cb.equal(root.get("isActive"), true));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
