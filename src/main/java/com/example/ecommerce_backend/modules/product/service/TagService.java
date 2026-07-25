package com.example.ecommerce_backend.modules.product.service;

import com.example.ecommerce_backend.core.annotation.RequiresPermission;
import com.example.ecommerce_backend.modules.product.dto.request.TagRequest;
import com.example.ecommerce_backend.modules.product.dto.response.TagResponse;
import com.example.ecommerce_backend.modules.product.entity.Tag;
import com.example.ecommerce_backend.modules.product.exception.TagNotFoundException;
import com.example.ecommerce_backend.modules.product.mapper.TagMapper;
import com.example.ecommerce_backend.modules.product.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getAll(Boolean active) {
        return tagRepository.findAll().stream()
                .filter(t -> active == null || t.isActive() == active)
                .map(TagMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequiresPermission("tag:write")
    public TagResponse create(TagRequest request) {
        Tag tag = Tag.builder()
                .name(request.getName())
                .slug(generateUniqueSlug(request.getName()))
                .build();
        tag = tagRepository.save(tag);
        return TagMapper.toResponse(tag);
    }

    @Transactional
    @RequiresPermission("tag:write")
    public TagResponse update(String uuid, TagRequest request) {
        Tag tag = tagRepository.findByUuid(uuid)
                .orElseThrow(() -> new TagNotFoundException(uuid));

        String newSlug = generateSlug(request.getName());
        if (!tag.getSlug().equals(newSlug) && tagRepository.existsBySlug(newSlug)) {
            newSlug = generateUniqueSlug(request.getName());
        }
        tag.setName(request.getName());
        tag.setSlug(newSlug);
        tag = tagRepository.save(tag);
        return TagMapper.toResponse(tag);
    }

    @Transactional
    @RequiresPermission("tag:write")
    public boolean toggleStatus(String uuid, boolean isActive) {
        Tag tag = tagRepository.findByUuid(uuid)
                .orElseThrow(() -> new TagNotFoundException(uuid));
        if (tag.isActive() == isActive) {
            return false;
        }
        tag.setActive(isActive);
        tagRepository.save(tag);
        return true;
    }

    @Transactional
    @RequiresPermission("tag:write")
    public void delete(String uuid) {
        Tag tag = tagRepository.findByUuid(uuid)
                .orElseThrow(() -> new TagNotFoundException(uuid));
        tagRepository.delete(tag);
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = generateSlug(name);
        String slug = baseSlug;
        int counter = 1;
        while (tagRepository.existsBySlug(slug)) {
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
