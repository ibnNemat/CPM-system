package uz.devops.intern.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import uz.devops.intern.domain.CustomerTelegram;

public interface CustomerTelegramRepositoryWithBagRelationships {
    Optional<CustomerTelegram> fetchBagRelationships(Optional<CustomerTelegram> customerTelegram);

    List<CustomerTelegram> fetchBagRelationships(List<CustomerTelegram> customerTelegrams);

    Page<CustomerTelegram> fetchBagRelationships(Page<CustomerTelegram> customerTelegrams);
}
