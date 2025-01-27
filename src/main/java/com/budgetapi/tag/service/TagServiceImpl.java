package com.budgetapi.tag.service;

import com.budgetapi.erro.NotFoundException;
import com.budgetapi.tag.dto.TagDTO;
import com.budgetapi.tag.dto.TagRequestDTO;
import com.budgetapi.tag.mapper.TagMapper;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.tag.repository.TagRepository;
import com.budgetapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    public static final String TAG_NOT_FOUND = "Tag with id %s not found";

    private final TagMapper mapper;
    private final UserService userService;
    private final TagRepository repository;

    @Override
    @Transactional
    public void save(TagRequestDTO dto) {
        Tag tag = this.mapper.toModel(dto, userService.getCurrentUser());
        repository.save(tag);
    }

    @Override
    @Transactional
    public void update(UUID id, TagRequestDTO dto) {
        Tag tag = findByIdAndCurrentUser(id);
        mapper.updateModel(dto, tag);
        repository.save(tag);
    }

    @Override
    public TagDTO findById(UUID id) {
        return mapper.toDTO(findByIdAndCurrentUser(id));
    }

    @Override
    public void delete(UUID id) {
        Tag tag = findByIdAndCurrentUser(id);
        repository.delete(tag);
    }

    @Override
    public Set<TagDTO> findAll(boolean includeDeleted) {
        Set<Tag> tags = includeDeleted
                ? repository.findAllByUser(userService.getCurrentUser())
                : repository.findAllByUserAndDeletedIsFalse(userService.getCurrentUser());
        return mapper.toDTO(tags);
    }

    private Tag findByIdAndCurrentUser(UUID id) {
        return repository.findByIdAndUser(id, userService.getCurrentUser())
                .orElseThrow(() -> new NotFoundException(String.format(TAG_NOT_FOUND, id)));
    }


}
