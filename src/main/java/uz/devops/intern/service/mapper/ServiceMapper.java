package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.ServicesDTO;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceMapper {
    public static Services toEntity(ServicesDTO servicesDTO){
        if (servicesDTO == null) return null;

        Services services = new Services();
        services.setId(servicesDTO.getId());
        services.setName(servicesDTO.getName());
        services.setPrice(servicesDTO.getPrice());
        services.setStartedPeriod(servicesDTO.getStartedPeriod());
        services.setPeriodType(servicesDTO.getPeriodType());
        services.setCountPeriod(servicesDTO.getCountPeriod());
        services.setTotalCountService(servicesDTO.getTotalCountService());
        services.setGroups(GroupMapper.groupsEntitySet(servicesDTO.getGroups()));
        return services;
    }

    public static Services toEntityWithoutGroup(ServicesDTO servicesDTO){
        if (servicesDTO == null) return null;

        Services services = new Services();
        services.setId(servicesDTO.getId());
        services.setName(servicesDTO.getName());
        services.setPrice(servicesDTO.getPrice());
        services.setStartedPeriod(servicesDTO.getStartedPeriod());
        services.setPeriodType(servicesDTO.getPeriodType());
        services.setCountPeriod(servicesDTO.getCountPeriod());
        services.setTotalCountService(servicesDTO.getTotalCountService());
        return services;
    }

    public static ServicesDTO toDtoForSaveServiceMethod(Services s) {
        if ( s == null ) {
            return null;
        }

        ServicesDTO servicesDTO = new ServicesDTO();
        servicesDTO.setGroups(GroupMapper.groupsDTOSet(s.getGroups()));
        return getServicesDTO(s, servicesDTO);
    }

    public static ServicesDTO toDtoForGetting(Services s) {
        if ( s == null ) {
            return null;
        }

        ServicesDTO servicesDTO = new ServicesDTO();
        servicesDTO.setId(s.getId());
        servicesDTO.setGroups(GroupMapper.groupsDTOSet(s.getGroups()));
        return getServicesDTO(s, servicesDTO);
    }

    @NotNull
    private static ServicesDTO getServicesDTO(Services s, ServicesDTO servicesDTO) {
        servicesDTO.setId( s.getId() );
        servicesDTO.setName( s.getName() );
        servicesDTO.setPrice( s.getPrice() );
        servicesDTO.setPeriodType( s.getPeriodType() );
        servicesDTO.setCountPeriod( s.getCountPeriod() );
        servicesDTO.setStartedPeriod(s.getStartedPeriod());
        servicesDTO.setTotalCountService(s.getTotalCountService());
        return servicesDTO;
    }

    public static Set<ServicesDTO> servicesDTOSet(Set<Services> services){
        return services.stream()
            .map(ServiceMapper::toDtoForGetting)
            .collect(Collectors.toSet());
    }

    public static Set<ServicesDTO> servicesDTOSetWithoutGroups(Set<Services> services){
        return services.stream()
            .map(ServiceMapper::toDtoWithoutGroups)
            .collect(Collectors.toSet());
    }

    public static ServicesDTO toDtoWithoutGroups(Services s) {
        if ( s == null ) {
            return null;
        }

        ServicesDTO servicesDTO = new ServicesDTO();
        return getServicesDTO(s, servicesDTO);
    }
}
