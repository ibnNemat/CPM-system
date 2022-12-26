package uz.devops.intern.service.manualMappper;

import org.springframework.stereotype.Component;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.TelegramGroup;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.CustomersDTO;
import uz.devops.intern.service.dto.TelegramGroupDTO;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomerTelegramsMapper {
    public CustomerTelegramDTO toDto(CustomerTelegram s) {
        if ( s == null ) {
            return null;
        }

        CustomerTelegramDTO.CustomerTelegramDTOBuilder customerTelegramDTO = CustomerTelegramDTO.builder();

        customerTelegramDTO.customer( toDtoCustomersId( s.getCustomer() ) );
        customerTelegramDTO.telegramGroups( toDtoTelegramGroupIdSet( s.getTelegramGroups() ) );
        customerTelegramDTO.id( s.getId() );
        customerTelegramDTO.isBot( s.getIsBot() );
        customerTelegramDTO.firstname( s.getFirstname() );
        customerTelegramDTO.lastname( s.getLastname() );
        customerTelegramDTO.username( s.getUsername() );
        customerTelegramDTO.telegramId( s.getTelegramId() );
        customerTelegramDTO.phoneNumber( s.getPhoneNumber() );
        customerTelegramDTO.step( s.getStep() );
        customerTelegramDTO.canJoinGroups( s.getCanJoinGroups() );
        customerTelegramDTO.languageCode( s.getLanguageCode() );
        customerTelegramDTO.isActive( s.getIsActive() );
        customerTelegramDTO.isManager(s.getManager());

        return customerTelegramDTO.build();
    }

    public CustomersDTO toDtoCustomersId(Customers customers) {
        if ( customers == null ) {
            return null;
        }

        CustomersDTO customersDTO = new CustomersDTO();

        customersDTO.setId( customers.getId() );

        return customersDTO;
    }

    public Set<TelegramGroupDTO> toDtoTelegramGroupIdSet(Set<TelegramGroup> telegramGroup) {
        return telegramGroup.stream().map(this::toDtoTelegramGroupId).collect(Collectors.toSet());
    }
    public TelegramGroupDTO toDtoTelegramGroupId(TelegramGroup telegramGroup) {
        if ( telegramGroup == null ) {
            return null;
        }

        TelegramGroupDTO.TelegramGroupDTOBuilder telegramGroupDTO = TelegramGroupDTO.builder();

        telegramGroupDTO.id( telegramGroup.getId() );

        return telegramGroupDTO.build();
    }

}
