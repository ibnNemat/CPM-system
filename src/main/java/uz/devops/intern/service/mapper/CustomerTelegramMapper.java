package uz.devops.intern.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.TelegramGroup;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.TelegramGroupDTO;

/**
 * Mapper for the entity {@link CustomerTelegram} and its DTO {@link CustomerTelegramDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerTelegramMapper extends EntityMapper<CustomerTelegramDTO, CustomerTelegram> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customersId")
    @Mapping(target = "telegramGroups", source = "telegramGroups", qualifiedByName = "telegramGroupIdSet")
    CustomerTelegramDTO toDto(CustomerTelegram s);

    @Mapping(target = "removeTelegramGroup", ignore = true)
    CustomerTelegram toEntity(CustomerTelegramDTO customerTelegramDTO);

    @Named("customersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomersDTO toDtoCustomersId(Customers customers);

    @Named("telegramGroupId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TelegramGroupDTO toDtoTelegramGroupId(TelegramGroup telegramGroup);

    @Named("telegramGroupIdSet")
    default Set<TelegramGroupDTO> toDtoTelegramGroupIdSet(Set<TelegramGroup> telegramGroup) {
        return telegramGroup.stream().map(this::toDtoTelegramGroupId).collect(Collectors.toSet());
    }
}
