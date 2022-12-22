package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Groups;
import uz.devops.intern.service.dto.GroupsDTO;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupMapper {

    public static GroupsDTO toDtoForSaveServiceMethod(Groups g){
        if (g == null){return null;}

        GroupsDTO groupsDTO = new GroupsDTO();
        groupsDTO.setId(g.getId());
        groupsDTO.setName(g.getName());
        groupsDTO.setGroupOwnerName(g.getGroupOwnerName());
        groupsDTO.setCustomers(CustomerMapper.toSetCustomerDto(g.getCustomers()));
        groupsDTO.setServices(ServiceMapper.servicesDTOSetWithoutGroups(g.getServices()));
        return groupsDTO;
    }

    public static Set<GroupsDTO> groupsDTOSet(Set<Groups> groupsSet){
        return groupsSet.stream()
            .map(GroupMapper::toDtoForSaveServiceMethod)
            .collect(Collectors.toSet());
    }

    public static GroupsDTO toDto(Groups g){
        GroupsDTO groupsDTO = new GroupsDTO();
        if (g != null) {
            groupsDTO.setId(g.getId());
            groupsDTO.setName(g.getName());
            groupsDTO.setGroupOwnerName(g.getGroupOwnerName());
            groupsDTO.setOrganization(OrganizationsMapper.toDtoWithoutGroups(g.getOrganization()));
            if(g.getCustomers() != null)
                groupsDTO.setCustomers(CustomerMapper.toSetCustomerDto(g.getCustomers()));
            if (g.getServices() != null)
                groupsDTO.setServices(ServiceMapper.servicesDTOSetWithoutGroups(g.getServices()));
        }
        return groupsDTO;
    }

    public static Groups toEntity(GroupsDTO g){
        Groups groups = new Groups();
        if (g != null) {
            groups.setId(g.getId());
            groups.setName(g.getName());
            groups.setGroupOwnerName(g.getGroupOwnerName());
            groups.setOrganization(OrganizationsMapper.toEntity(g.getOrganization()));
        }
        return groups;
    }

    public static Set<Groups> groupsEntitySet(Set<GroupsDTO> groupsSet){
        return groupsSet.stream()
            .map(GroupMapper::toEntityForSavingService)
            .collect(Collectors.toSet());
    }
    public static Groups toEntityForSavingService(GroupsDTO g){
        Groups groups = new Groups();
        groups.setId(g.getId());
        return groups;
    }

    public static GroupsDTO toDtoForSavingGroup(Groups group){
        return GroupsDTO.builder()
            .id(group.getId())
            .name(group.getName())
            .build();
    }
}
