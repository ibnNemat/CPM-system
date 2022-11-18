package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.UserDTO;

/**
 * Mapper for the entity {@link Customers} and its DTO {@link CustomersDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomersMapper extends EntityMapper<CustomersDTO, Customers> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    CustomersDTO toDto(Customers s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
