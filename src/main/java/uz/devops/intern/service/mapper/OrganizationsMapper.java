package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Organization;
import uz.devops.intern.service.dto.OrganizationDTO;

public class OrganizationsMapper {
    public static OrganizationDTO toDto(Organization o){
        OrganizationDTO organizationDTO = new OrganizationDTO();
        if (o != null) {
            organizationDTO.setId(o.getId());
            organizationDTO.setName(o.getName());
            organizationDTO.setOrgOwnerName(o.getOrgOwnerName());
        }
        return organizationDTO;
    }

    public static OrganizationDTO toDtoWithGroups(Organization o){
        OrganizationDTO organizationDTO = new OrganizationDTO();
        if (o != null) {
            organizationDTO.setId(o.getId());
            organizationDTO.setName(o.getName());
            organizationDTO.setOrgOwnerName(o.getOrgOwnerName());
            organizationDTO.setGroups(GroupMapper.groupsDTOSet(o.getGroups()));
        }
        return organizationDTO;
    }

    public static Organization toEntity(OrganizationDTO o){
        Organization organization = new Organization();
        organization.setName(o.getName());
        organization.setOrgOwnerName(o.getOrgOwnerName());
        return organization;
    }
}
