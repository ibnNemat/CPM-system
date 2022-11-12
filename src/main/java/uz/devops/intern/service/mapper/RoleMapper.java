package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.Role;
import uz.devops.intern.service.dto.RoleDTO;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {}
