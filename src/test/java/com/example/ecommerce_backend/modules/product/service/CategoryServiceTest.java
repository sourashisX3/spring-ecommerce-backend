package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.modules.product.dto.response.CategoryResponse;
import com.example.ecommerce_backend.modules.product.entity.Category;
import com.example.ecommerce_backend.modules.product.exception.CategoryHasChildrenException;
import com.example.ecommerce_backend.modules.product.exception.CategoryHasProductsException;
import com.example.ecommerce_backend.modules.product.exception.CategoryNotFoundException;
import com.example.ecommerce_backend.modules.product.exception.CircularCategoryReferenceException;
import com.example.ecommerce_backend.modules.product.repository.CategoryRepository;
import com.example.ecommerce_backend.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category root;
    private Category child;
    private Category inactiveRoot;
    private Category inactiveChild;

    @BeforeEach
    void setUp() {
        root = Category.builder().id(1L).name("Root").slug("root").sortOrder(1).isActive(true).build();
        child = Category.builder().id(2L).name("Child").slug("child").sortOrder(1).isActive(true).parent(root).build();
        root.setChildren(new ArrayList<>(List.of(child)));

        inactiveRoot = Category.builder().id(3L).name("Inactive Root").slug("inactive-root").sortOrder(2).isActive(false).build();
        inactiveChild = Category.builder().id(4L).name("Inactive Child").slug("inactive-child").sortOrder(1).isActive(false).parent(inactiveRoot).build();
        inactiveRoot.setChildren(new ArrayList<>(List.of(inactiveChild)));
    }

    // --- getAll ---

    @Test
    void getAll_whenActiveNull_shouldReturnAll() {
        when(categoryRepository.findAll()).thenReturn(List.of(root, child, inactiveRoot, inactiveChild));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        List<CategoryResponse> result = categoryService.getAll(null);

        assertThat(result).hasSize(4);
    }

    @Test
    void getAll_whenActiveTrue_shouldReturnOnlyActive() {
        when(categoryRepository.findAll()).thenReturn(List.of(root, child, inactiveRoot, inactiveChild));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        List<CategoryResponse> result = categoryService.getAll(true);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(CategoryResponse::isActive);
    }

    @Test
    void getAll_whenActiveFalse_shouldReturnOnlyInactive() {
        when(categoryRepository.findAll()).thenReturn(List.of(root, child, inactiveRoot, inactiveChild));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        List<CategoryResponse> result = categoryService.getAll(false);

        assertThat(result).hasSize(2);
        assertThat(result).noneMatch(CategoryResponse::isActive);
    }

    @Test
    void getAll_shouldSetProductCount() {
        when(categoryRepository.findAll()).thenReturn(List.of(root));
        when(productRepository.countByCategoryId(1L)).thenReturn(5L);

        List<CategoryResponse> result = categoryService.getAll(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductCount()).isEqualTo(5);
    }

    // --- getTree ---

    @Test
    void getTree_whenActiveNull_shouldReturnAllRootsWithAllChildren() {
        when(categoryRepository.findAll()).thenReturn(List.of(root, child, inactiveRoot, inactiveChild));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        List<CategoryResponse> result = categoryService.getTree(null);

        assertThat(result).hasSize(2);
        CategoryResponse rootResp = result.stream().filter(r -> r.getSlug().equals("root")).findFirst().orElseThrow();
        assertThat(rootResp.getChildren()).hasSize(1);

        CategoryResponse inactiveResp = result.stream().filter(r -> r.getSlug().equals("inactive-root")).findFirst().orElseThrow();
        assertThat(inactiveResp.getChildren()).hasSize(1);
    }

    @Test
    void getTree_whenActiveTrue_shouldReturnOnlyActiveRootsWithActiveChildren() {
        when(categoryRepository.findAll()).thenReturn(List.of(root, child, inactiveRoot, inactiveChild));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        List<CategoryResponse> result = categoryService.getTree(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSlug()).isEqualTo("root");
        assertThat(result.get(0).getChildren()).hasSize(1);
    }

    @Test
    void getTree_whenActiveFalse_shouldReturnOnlyInactiveRoots() {
        when(categoryRepository.findAll()).thenReturn(List.of(root, child, inactiveRoot, inactiveChild));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        List<CategoryResponse> result = categoryService.getTree(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSlug()).isEqualTo("inactive-root");
    }

    @Test
    void getTree_shouldHandleCycleSafely() {
        Category a = Category.builder().id(1L).name("A").slug("a").sortOrder(1).isActive(true).build();
        Category b = Category.builder().id(2L).name("B").slug("b").sortOrder(1).isActive(true).parent(a).build();
        a.setChildren(new ArrayList<>(List.of(b)));

        when(categoryRepository.findAll()).thenReturn(List.of(a, b));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        List<CategoryResponse> result = categoryService.getTree(null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChildren()).hasSize(1);
    }

    // --- getBySlug ---

    @Test
    void getBySlug_shouldReturnCategoryWithChildren() {
        when(categoryRepository.findBySlug("root")).thenReturn(Optional.of(root));
        when(categoryRepository.findAll()).thenReturn(List.of(root, child));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);

        CategoryResponse result = categoryService.getBySlug("root");
        assertThat(result.getSlug()).isEqualTo("root");
        assertThat(result.getChildren()).hasSize(1);
    }

    @Test
    void getBySlug_whenNotFound_shouldThrow() {
        when(categoryRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getBySlug("nonexistent"))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    // --- toggleStatus ---

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnFalse() {
        when(categoryRepository.findBySlug("root")).thenReturn(Optional.of(root));

        boolean changed = categoryService.toggleStatus("root", true);

        assertThat(changed).isFalse();
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenAlreadyInactive_shouldReturnFalse() {
        when(categoryRepository.findBySlug("inactive-root")).thenReturn(Optional.of(inactiveRoot));

        boolean changed = categoryService.toggleStatus("inactive-root", false);

        assertThat(changed).isFalse();
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void toggleStatus_shouldToggleFromActiveToInactive() {
        when(categoryRepository.findBySlug("root")).thenReturn(Optional.of(root));

        boolean changed = categoryService.toggleStatus("root", false);

        assertThat(changed).isTrue();
        assertThat(root.isActive()).isFalse();
        verify(categoryRepository).save(root);
    }

    @Test
    void toggleStatus_shouldToggleFromInactiveToActive() {
        when(categoryRepository.findBySlug("inactive-root")).thenReturn(Optional.of(inactiveRoot));

        boolean changed = categoryService.toggleStatus("inactive-root", true);

        assertThat(changed).isTrue();
        assertThat(inactiveRoot.isActive()).isTrue();
        verify(categoryRepository).save(inactiveRoot);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(categoryRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.toggleStatus("nonexistent", true))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_whenCategoryHasChildren_shouldThrow() {
        when(categoryRepository.findBySlug("root")).thenReturn(Optional.of(root));

        assertThatThrownBy(() -> categoryService.delete("root"))
                .isInstanceOf(CategoryHasChildrenException.class);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_whenCategoryHasProducts_shouldThrow() {
        Category leaf = Category.builder().id(5L).name("Leaf").slug("leaf").children(Collections.emptyList()).build();
        when(categoryRepository.findBySlug("leaf")).thenReturn(Optional.of(leaf));
        when(productRepository.countByCategoryId(5L)).thenReturn(3L);

        assertThatThrownBy(() -> categoryService.delete("leaf"))
                .isInstanceOf(CategoryHasProductsException.class);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_whenNoChildrenAndNoProducts_shouldDelete() {
        Category leaf = Category.builder().id(5L).name("Leaf").slug("leaf").children(Collections.emptyList()).build();
        when(categoryRepository.findBySlug("leaf")).thenReturn(Optional.of(leaf));
        when(productRepository.countByCategoryId(5L)).thenReturn(0L);

        categoryService.delete("leaf");

        verify(categoryRepository).delete(leaf);
    }

    // --- getDescendantCategoryIds ---

    @Test
    void getDescendantCategoryIds_shouldDelegateToRepo() {
        Set<Long> expected = Set.of(1L, 2L);
        when(categoryRepository.findDescendantIds("root")).thenReturn(expected);

        Set<Long> result = categoryService.getDescendantCategoryIds("root");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getDescendantCategoryIds_whenRepoThrows_shouldReturnEmpty() {
        when(categoryRepository.findDescendantIds("bad")).thenThrow(new RuntimeException("DB error"));

        Set<Long> result = categoryService.getDescendantCategoryIds("bad");

        assertThat(result).isEmpty();
    }

}
