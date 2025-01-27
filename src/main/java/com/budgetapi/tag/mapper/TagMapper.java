package com.budgetapi.tag.mapper;

import com.budgetapi.tag.dto.TagDTO;
import com.budgetapi.tag.dto.TagRequestDTO;
import com.budgetapi.tag.model.Tag;
import com.budgetapi.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagMapper MAPPER = Mappers.getMapper(TagMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    Tag toModel(TagRequestDTO dto, User user);

    void updateModel(TagRequestDTO dto, @MappingTarget Tag tag);

    TagDTO toDTO(Tag tag);

    Set<TagDTO> toDTO(Set<Tag> tags);
}
