package uz.devops.intern.service.mapper;

import org.mapstruct.*;
import uz.devops.intern.domain.Services;
import uz.devops.intern.service.dto.ServicesDTO;

/**
 * Mapper for the entity {@link Services} and its DTO {@link ServicesDTO}.
 */
@Mapper(componentModel = "spring")
public interface ServicesMapper extends EntityMapper<ServicesDTO, Services> {}
