package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Groups;
import uz.devops.intern.service.dto.GroupsDTO;

public class GroupMapper {
    public static GroupsDTO toDto(Groups g){
        GroupsDTO groupsDTO = new GroupsDTO();
        groupsDTO.setGroupOwnerName(g.getName());
        groupsDTO.setOrganization(OrganizationsMapper.toDto(g.getOrganization()));
        groupsDTO.setUsers(CustomerMapper.toEntity(g.getUsers()));
        return groupsDTO;
    }
}
