package uz.devops.intern.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import uz.devops.intern.domain.Services;

public interface ServicesRepositoryWithBagRelationships {

    List<Services> fetchBagRelationships(List<Services> services);

}
