package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.TelegramEntity;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.dto.TelegramEntityDTO;
import uz.devops.intern.service.dto.UserDTO;

/**
 * Mapper for the entity {@link TelegramEntity} and its DTO {@link TelegramEntityDTO}.
 */
@Mapper(componentModel = "spring")
public interface TelegramEntityMapper extends EntityMapper<TelegramEntityDTO, TelegramEntity> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    TelegramEntityDTO toDto(TelegramEntity s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
