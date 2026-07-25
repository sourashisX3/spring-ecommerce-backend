package com.example.ecommerce_backend.modules.product.repository;

import com.example.ecommerce_backend.modules.product.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category root;
    private Category child;
    private Category grandchild;

    @BeforeEach
    void setUp() {
        root = categoryRepository.save(Category.builder()
                .name("Electronics").slug("electronics").sortOrder(1).build());

        child = categoryRepository.save(Category.builder()
                .name("Laptops").slug("laptops").sortOrder(1)
                .parent(root).build());

        grandchild = categoryRepository.save(Category.builder()
                .name("Gaming Laptops").slug("gaming-laptops").sortOrder(1)
                .parent(child).build());
    }

    @Test
    void findDescendantIds_shouldReturnSelfForLeaf() {
        Set<Long> ids = categoryRepository.findDescendantIds("gaming-laptops");
        assertThat(ids).containsExactly(grandchild.getId());
    }

    @Test
    void findDescendantIds_shouldReturnSelfAndDirectChildren() {
        Set<Long> ids = categoryRepository.findDescendantIds("laptops");
        assertThat(ids).containsExactlyInAnyOrder(child.getId(), grandchild.getId());
    }

    @Test
    void findDescendantIds_shouldReturnAllDescendantsForRoot() {
        Set<Long> ids = categoryRepository.findDescendantIds("electronics");
        assertThat(ids).containsExactlyInAnyOrder(root.getId(), child.getId(), grandchild.getId());
    }

    @Test
    void findDescendantIds_shouldReturnEmptyForNonExistentSlug() {
        Set<Long> ids = categoryRepository.findDescendantIds("non-existent");
        assertThat(ids).isEmpty();
    }

    @Test
    void findAll_shouldEagerlyFetchParent() {
        List<Category> all = categoryRepository.findAll();

        Category laptops = all.stream().filter(c -> c.getSlug().equals("laptops")).findFirst().orElseThrow();
        assertThat(laptops.getParent()).isNotNull();
        assertThat(laptops.getParent().getSlug()).isEqualTo("electronics");
    }

    @Test
    void findBySlug_shouldReturnCategory() {
        Category found = categoryRepository.findBySlug("electronics").orElseThrow();
        assertThat(found.getName()).isEqualTo("Electronics");
    }

    @Test
    void findBySlug_shouldReturnEmptyForNonExistent() {
        assertThat(categoryRepository.findBySlug("non-existent")).isEmpty();
    }

    @Test
    void existsBySlug_shouldReturnTrueForExisting() {
        assertThat(categoryRepository.existsBySlug("electronics")).isTrue();
    }

    @Test
    void existsBySlug_shouldReturnFalseForNonExisting() {
        assertThat(categoryRepository.existsBySlug("non-existent")).isFalse();
    }
}
