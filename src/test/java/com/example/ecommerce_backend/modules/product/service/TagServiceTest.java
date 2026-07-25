package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.modules.product.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.product.entity.Tag;
import com.example.ecommerce_backend.modules.product.exception.TagNotFoundException;
import com.example.ecommerce_backend.modules.product.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag activeTag;
    private Tag inactiveTag;

    @BeforeEach
    void setUp() {
        activeTag = Tag.builder().id(1L).uuid("uuid-1").name("New").slug("new").isActive(true).build();
        inactiveTag = Tag.builder().id(2L).uuid("uuid-2").name("Old").slug("old").isActive(false).build();
    }

    @Test
    void getAll_whenActiveNull_shouldReturnAll() {
        when(tagRepository.findAll()).thenReturn(List.of(activeTag, inactiveTag));

        List<TagResponse> result = tagService.getAll(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAll_whenActiveTrue_shouldReturnOnlyActive() {
        when(tagRepository.findAll()).thenReturn(List.of(activeTag, inactiveTag));

        List<TagResponse> result = tagService.getAll(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    void getAll_whenActiveFalse_shouldReturnOnlyInactive() {
        when(tagRepository.findAll()).thenReturn(List.of(activeTag, inactiveTag));

        List<TagResponse> result = tagService.getAll(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isFalse();
    }

    @Test
    void toggleStatus_whenAlreadyActive_shouldReturnFalse() {
        when(tagRepository.findByUuid("uuid-1")).thenReturn(Optional.of(activeTag));

        boolean changed = tagService.toggleStatus("uuid-1", true);

        assertThat(changed).isFalse();
        verify(tagRepository, never()).save(any());
    }

    @Test
    void toggleStatus_whenAlreadyInactive_shouldReturnFalse() {
        when(tagRepository.findByUuid("uuid-2")).thenReturn(Optional.of(inactiveTag));

        boolean changed = tagService.toggleStatus("uuid-2", false);

        assertThat(changed).isFalse();
        verify(tagRepository, never()).save(any());
    }

    @Test
    void toggleStatus_shouldToggleActiveToInactive() {
        when(tagRepository.findByUuid("uuid-1")).thenReturn(Optional.of(activeTag));

        boolean changed = tagService.toggleStatus("uuid-1", false);

        assertThat(changed).isTrue();
        assertThat(activeTag.isActive()).isFalse();
        verify(tagRepository).save(activeTag);
    }

    @Test
    void toggleStatus_shouldToggleInactiveToActive() {
        when(tagRepository.findByUuid("uuid-2")).thenReturn(Optional.of(inactiveTag));

        boolean changed = tagService.toggleStatus("uuid-2", true);

        assertThat(changed).isTrue();
        assertThat(inactiveTag.isActive()).isTrue();
        verify(tagRepository).save(inactiveTag);
    }

    @Test
    void toggleStatus_whenNotFound_shouldThrow() {
        when(tagRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.toggleStatus("nonexistent", true))
                .isInstanceOf(TagNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteTag() {
        when(tagRepository.findByUuid("uuid-1")).thenReturn(Optional.of(activeTag));

        tagService.delete("uuid-1");

        verify(tagRepository).delete(activeTag);
    }

    @Test
    void delete_whenNotFound_shouldThrow() {
        when(tagRepository.findByUuid("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.delete("nonexistent"))
                .isInstanceOf(TagNotFoundException.class);
    }
}