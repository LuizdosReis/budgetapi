package com.budgetapi.tag.service;

import com.budgetapi.tag.dto.TagDTO;
import com.budgetapi.tag.dto.TagRequestDTO;

import java.util.Set;
import java.util.UUID;

public interface TagService {
    void save(TagRequestDTO dto);

    void update(UUID id, TagRequestDTO dto);

    TagDTO findById(UUID id);

    void delete(UUID id);

    Set<TagDTO> findAll(boolean includeDeleted);
}
