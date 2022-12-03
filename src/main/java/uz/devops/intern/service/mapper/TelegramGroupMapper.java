package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.TelegramGroup;
import uz.devops.intern.service.dto.TelegramGroupDTO;

/**
 * Mapper for the entity {@link TelegramGroup} and its DTO {@link TelegramGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface TelegramGroupMapper extends EntityMapper<TelegramGroupDTO, TelegramGroup> {}
