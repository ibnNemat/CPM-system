package uz.devops.intern.service.mapper;

import uz.devops.intern.domain.*;
import uz.devops.intern.service.dto.*;

import java.util.List;

public class PaymentsMapper {

    public static Payment toEntity(PaymentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Payment payment = new Payment();

        payment.setGroup( GroupMapper.toEntity( dto.getGroup() ) );
        payment.setId( dto.getId() );
        payment.setPaidMoney( dto.getPaidMoney() );
        payment.setPaymentForPeriod( dto.getPaymentForPeriod() );
        payment.setIsPayed( dto.getIsPayed() );
        payment.setStartedPeriod( dto.getStartedPeriod() );
        payment.setFinishedPeriod( dto.getFinishedPeriod() );
        payment.customer( CustomerMapper.toEntityWithNoUser(dto.getCustomer()));
        payment.service( ServiceMapper.toEntityWithoutGroup(dto.getService()) );

        return payment;
    }

    public static CustomersDTO customersToCustomersDTO(Customers customers) {
        if ( customers == null ) {
            return null;
        }

        CustomersDTO customersDTO = new CustomersDTO();

        customersDTO.setId( customers.getId() );
        customersDTO.setUsername( customers.getUsername() );
        customersDTO.setPassword( customers.getPassword() );
        customersDTO.setPhoneNumber( customers.getPhoneNumber() );
        customersDTO.setBalance( customers.getBalance() );
        customersDTO.setUser( userToUserDTO( customers.getUser() ) );

        return customersDTO;
    }

    public static UserDTO userToUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );
        userDTO.setLogin( user.getLogin() );

        return userDTO;
    }

    public static List<PaymentDTO> paymentDTOList(List<Payment> paymentList){
        if (paymentList == null)
            return null;
        return paymentList.stream()
            .map(PaymentsMapper::toDto)
            .toList();
    }

    public static PaymentDTO toDto(Payment s) {
        if ( s == null ) {
            return null;
        }

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCustomer( customersToCustomersDTO( s.getCustomer() ) );
        paymentDTO.setService( servicesToServicesDTO(s.getService()));
        paymentDTO.setGroup( groupsToGroupsDTO( s.getGroup() ) );
        paymentDTO.setId( s.getId() );
        paymentDTO.setPaidMoney( s.getPaidMoney() );
        paymentDTO.setPaymentForPeriod( s.getPaymentForPeriod() );
        paymentDTO.setIsPayed( s.getIsPayed() );
        paymentDTO.setStartedPeriod( s.getStartedPeriod() );
        paymentDTO.setFinishedPeriod( s.getFinishedPeriod() );

        return paymentDTO;
    }

    public static GroupsDTO groupsToGroupsDTO(Groups groups) {
        if ( groups == null ) {
            return null;
        }

        GroupsDTO groupsDTO = new GroupsDTO();
        groupsDTO.setId( groups.getId() );
        groupsDTO.setName( groups.getName() );
        groupsDTO.setGroupOwnerName( groups.getGroupOwnerName() );
        groupsDTO.setOrganization( organizationToOrganizationDTO( groups.getOrganization() ) );

        return groupsDTO;
    }

    public static OrganizationDTO organizationToOrganizationDTO(Organization organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId( organization.getId() );
        organizationDTO.setName( organization.getName() );
        organizationDTO.setOrgOwnerName( organization.getOrgOwnerName() );

        return organizationDTO;
    }

    public static ServicesDTO servicesToServicesDTO(Services services) {
        if ( services == null ) {
            return null;
        }

        ServicesDTO servicesDTO = new ServicesDTO();

        servicesDTO.setId( services.getId() );
        servicesDTO.setName( services.getName() );
        servicesDTO.setPrice( services.getPrice() );
        servicesDTO.setStartedPeriod( services.getStartedPeriod() );
        servicesDTO.setPeriodType( services.getPeriodType() );
        servicesDTO.setCountPeriod( services.getCountPeriod() );
        return servicesDTO;
    }
}
