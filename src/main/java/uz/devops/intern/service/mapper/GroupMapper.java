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
        groupsDTO.setParentId(g.getParentId());
        groupsDTO.setCustomers(CustomerMapper.toSetCustomerDto(g.getCustomers()));
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
            groupsDTO.setParentId(g.getParentId());
            groupsDTO.setOrganization(OrganizationsMapper.toDto(g.getOrganization()));
            groupsDTO.setCustomers(CustomerMapper.toSetCustomerDto(g.getCustomers()));
        }
        return groupsDTO;
    }

    public static Groups toEntityForSavingService(GroupsDTO g){
        Groups groups = new Groups();
        groups.setId(g.getId());
        return groups;
    }
}
