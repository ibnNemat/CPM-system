package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.BotToken;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.service.dto.UserDTO;

/**
 * Mapper for the entity {@link BotToken} and its DTO {@link BotTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface BotTokenMapper extends EntityMapper<BotTokenDTO, BotToken> {
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "userId")
    BotTokenDTO toDto(BotToken s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
