package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.ServicesDTO;

public class ServiceMapper {
    public static Services toEntity(ServicesDTO servicesDTO){
        Services services = new Services();
        if (servicesDTO.getId() != null){
            services.setId(servicesDTO.getId());
        }
        services.setServiceType(servicesDTO.getServiceType());
        services.setPrice(servicesDTO.getPrice());
        services.setPeriodType(servicesDTO.getPeriodType());
        services.setCountPeriod(servicesDTO.getCountPeriod());
        services.setGroup(GroupMapper.toEntityForSavingService(servicesDTO.getGroup()));
        services.setUsers(CustomerMapper.toSetCustomerEntityForSavingService(servicesDTO.getUsers()));
        return services;
    }

    public static ServicesDTO toDto(Services s) {
        if ( s == null ) {
            return null;
        }

        ServicesDTO servicesDTO = new ServicesDTO();

        servicesDTO.setGroup(GroupMapper.ForSavingService(s.getGroup()));
        servicesDTO.setUsers(CustomerMapper.toSetCustomerDtoForSavingService(s.getUsers()));
        servicesDTO.setId( s.getId() );
        servicesDTO.setServiceType( s.getServiceType() );
        servicesDTO.setPrice( s.getPrice() );
        servicesDTO.setPeriodType( s.getPeriodType() );
        servicesDTO.setCountPeriod( s.getCountPeriod() );

        return servicesDTO;
    }


}
