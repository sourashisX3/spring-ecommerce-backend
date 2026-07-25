package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.product.dto.request.CategoryRequest;
import com.example.ecommerce_backend.modules.product.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.product.entity.Category;
import com.example.ecommerce_backend.modules.product.exception.CategoryHasChildrenException;
import com.example.ecommerce_backend.modules.product.exception.CategoryHasProductsException;
import com.example.ecommerce_backend.modules.product.exception.CategoryNotFoundException;
import com.example.ecommerce_backend.modules.product.exception.CircularCategoryReferenceException;
import com.example.ecommerce_backend.modules.product.mapper.CategoryMapper;
import com.example.ecommerce_backend.modules.product.repository.CategoryRepository;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(c -> {
                    CategoryResponse resp = CategoryMapper.toResponse(c);
                    resp.setProductCount(productRepository.countByCategoryId(c.getId()));
                    return resp;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getTree() {
        List<Category> all = categoryRepository.findAll();

        Map<Long, List<Category>> childrenMap = new HashMap<>();
        Map<Long, Long> productCounts = new HashMap<>();

        for (Category c : all) {
            productCounts.put(c.getId(), productRepository.countByCategoryId(c.getId()));
            if (c.getParent() != null) {
                childrenMap.computeIfAbsent(c.getParent().getId(), k -> new ArrayList<>()).add(c);
            }
        }

        childrenMap.values().forEach(list -> list.sort(Comparator.comparingInt(Category::getSortOrder)));

        List<Category> roots = all.stream()
                .filter(c -> c.getParent() == null)
                .sorted(Comparator.comparingInt(Category::getSortOrder))
                .collect(Collectors.toList());

        Set<Long> visited = new HashSet<>();
        return roots.stream()
                .map(root -> buildTree(root, childrenMap, visited, productCounts))
                .collect(Collectors.toList());
    }

    private CategoryResponse buildTree(Category category, Map<Long, List<Category>> childrenMap,
                                        Set<Long> visited, Map<Long, Long> productCounts) {
        if (!visited.add(category.getId())) {
            CategoryResponse safe = CategoryMapper.toResponse(category);
            safe.setProductCount(productCounts.getOrDefault(category.getId(), 0L));
            safe.setChildren(Collections.emptyList());
            return safe;
        }

        CategoryResponse response = CategoryMapper.toResponse(category);
        response.setProductCount(productCounts.getOrDefault(category.getId(), 0L));

        List<Category> children = childrenMap.getOrDefault(category.getId(), Collections.emptyList());
        if (!children.isEmpty()) {
            response.setChildren(children.stream()
                    .map(child -> buildTree(child, childrenMap, visited, productCounts))
                    .collect(Collectors.toList()));
        }

        return response;
    }

    @Transactional(readOnly = true)
    public CategoryResponse getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException(slug));

        List<Category> all = categoryRepository.findAll();

        Map<Long, List<Category>> childrenMap = new HashMap<>();
        Map<Long, Long> productCounts = new HashMap<>();

        for (Category c : all) {
            productCounts.put(c.getId(), productRepository.countByCategoryId(c.getId()));
            if (c.getParent() != null) {
                childrenMap.computeIfAbsent(c.getParent().getId(), k -> new ArrayList<>()).add(c);
            }
        }

        childrenMap.values().forEach(list -> list.sort(Comparator.comparingInt(Category::getSortOrder)));

        Set<Long> visited = new HashSet<>();
        return buildTree(category, childrenMap, visited, productCounts);
    }

    @Transactional(readOnly = true)
    public Set<Long> getDescendantCategoryIds(String slug) {
        try {
            return categoryRepository.findDescendantIds(slug);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Transactional
    @RequiresPermission("category:write")
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(generateUniqueSlug(request.getName()));
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());

        if (request.getParentSlug() != null && !request.getParentSlug().isBlank()) {
            Category parent = categoryRepository.findBySlug(request.getParentSlug())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentSlug()));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return CategoryMapper.toResponse(category);
    }

    @Transactional
    @RequiresPermission("category:write")
    public CategoryResponse update(String slug, CategoryRequest request) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException(slug));

        category.setName(request.getName());
        if (!category.getSlug().equals(generateSlug(request.getName()))) {
            category.setSlug(generateUniqueSlug(request.getName()));
        }
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());

        if (request.getParentSlug() != null && !request.getParentSlug().isBlank()) {
            Category newParent = categoryRepository.findBySlug(request.getParentSlug())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentSlug()));
            validateNoCircularReference(category, newParent);
            category.setParent(newParent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return CategoryMapper.toResponse(category);
    }

    @Transactional
    @RequiresPermission("category:write")
    public void delete(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException(slug));

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            throw new CategoryHasChildrenException(slug);
        }

        if (productRepository.countByCategoryId(category.getId()) > 0) {
            throw new CategoryHasProductsException(slug);
        }

        categoryRepository.delete(category);
    }

    private void validateNoCircularReference(Category category, Category newParent) {
        if (category.getId().equals(newParent.getId())) {
            throw new CircularCategoryReferenceException();
        }
        Category current = newParent;
        while (current.getParent() != null) {
            if (current.getParent().getId().equals(category.getId())) {
                throw new CircularCategoryReferenceException();
            }
            current = current.getParent();
        }
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = generateSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (categoryRepository.existsBySlug(slug)) {
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
}
