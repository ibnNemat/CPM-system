package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Groups;
import uz.devops.intern.service.dto.GroupsDTO;

public class GroupMapper {
    public static GroupsDTO ForSavingService(Groups g){
        GroupsDTO groupsDTO = new GroupsDTO();
        groupsDTO.setId(g.getId());
        groupsDTO.setName(g.getName());
        groupsDTO.setGroupOwnerName(g.getGroupOwnerName());
        groupsDTO.setUsers(CustomerMapper.toSetCustomerDto(g.getUsers()));
        return groupsDTO;
    }

    public static GroupsDTO ForSavingGroup(Groups g){
        GroupsDTO groupsDTO = new GroupsDTO();
        groupsDTO.setId(g.getId());
        groupsDTO.setName(g.getName());
        groupsDTO.setGroupOwnerName(g.getGroupOwnerName());
        groupsDTO.setOrganization(OrganizationsMapper.toDto(g.getOrganization()));
        groupsDTO.setUsers(CustomerMapper.toSetCustomerDto(g.getUsers()));
        return groupsDTO;
    }

    public static Groups toEntityForSavingService(GroupsDTO g){
        Groups groups = new Groups();
        groups.setId(g.getId());
        return groups;
    }
}
