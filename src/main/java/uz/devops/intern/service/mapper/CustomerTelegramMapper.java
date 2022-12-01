package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.UserDTO;

/**
 * Mapper for the entity {@link CustomerTelegram} and its DTO {@link CustomerTelegramDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerTelegramMapper extends EntityMapper<CustomerTelegramDTO, CustomerTelegram> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    CustomerTelegramDTO toDto(CustomerTelegram s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
