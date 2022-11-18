package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.Organization;
import uz.devops.intern.service.dto.OrganizationDTO;

public class OrganizationsMapper {
    public static OrganizationDTO toDto(Organization o){
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(o.getId());
        organizationDTO.setName(organizationDTO.getName());
        organizationDTO.setOrgOwnerName(organizationDTO.getOrgOwnerName());

        return organizationDTO;
    }
}
