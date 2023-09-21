package com.example.trafileatestproject.util;

import java.util.List;

public interface Mapper<DTO, Entity> {
    DTO toDto(Entity entity);

    List<DTO> toDtos(List<Entity> entities);
    Entity toEntity(DTO dto);
}

