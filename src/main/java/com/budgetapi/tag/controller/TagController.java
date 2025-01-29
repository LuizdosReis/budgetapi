package com.budgetapi.tag.controller;

import com.budgetapi.tag.dto.TagDTO;
import com.budgetapi.tag.dto.TagRequestDTO;
import com.budgetapi.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping(TagController.BASE_URL)
public class TagController {

    public static final String BASE_URL = "/tags";

    private final TagService tagService;

    @GetMapping()
    public Set<TagDTO> getAll(@RequestParam(required = false, defaultValue = "false") boolean includeDeleted) {
        return tagService.findAll(includeDeleted);
    }

    @GetMapping("/{id}")
    public TagDTO getById(@PathVariable UUID id) {
        return tagService.findById(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(@RequestBody @Valid TagRequestDTO dto) {
        tagService.save(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable UUID id, @RequestBody @Valid TagRequestDTO dto) {
        tagService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        tagService.delete(id);
    }

}
