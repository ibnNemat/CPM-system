package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.*;
import uz.devops.intern.repository.PaymentRepository;
import uz.devops.intern.repository.ServicesRepository;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.PaymentHistoryService;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.mapper.PaymentMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static uz.devops.intern.service.dto.ResponseCode.*;

/**
 * Service Implementation for managing {@link uz.devops.intern.domain.Payment}.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final CustomersService customersService;
    private final ServicesRepository servicesRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final GroupsService groupsService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper, CustomersService customersService, ServicesRepository servicesRepository, PaymentHistoryService paymentHistoryService, GroupsService groupsService) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.customersService = customersService;
        this.servicesRepository = servicesRepository;
        this.paymentHistoryService = paymentHistoryService;
        this.groupsService = groupsService;
    }

    @Override
    public PaymentDTO save(PaymentDTO paymentDTO) {
        log.debug("Request to save PaymentDTO : {}", paymentDTO);
        uz.devops.intern.domain.Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public List<Payment> saveAll(List<Payment> paymentList) {
        log.debug("Request to save List<Payment> : {}", paymentList);
        paymentRepository.saveAll(paymentList);
        return paymentList;
    }


    @Override
    public PaymentDTO update(PaymentDTO paymentDTO) {
        log.debug("Request to update PaymentDTO : {}", paymentDTO);
        uz.devops.intern.domain.Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    public ResponseDTO payForService(PaymentDTO paymentDTO) {
        try {
            log.debug("Request to pay for Service : {}", paymentDTO);

            ResponseDTO<Double> responseDTO = checkCustomerAccount(paymentDTO);
            if (!responseDTO.getSuccess()) {
                return responseDTO;
            }

            Payment payment = paymentMapper.toEntity(paymentDTO);
            Customers c = payment.getCustomer();
            Groups group = payment.getGroup();
            Services service = payment.getService();
            Optional<Services> servicesOptional = servicesRepository.findById(service.getId());

            if (servicesOptional.isEmpty()) {
                return new ResponseDTO(NOT_FOUND, ResponseMessage.NOT_FOUND, false, null);
            }

            service = servicesOptional.get();
            LocalDate startedDate = payment.getStartedPeriod();
            Double payedMoney = payment.getPayedMoney();

            Optional<Payment> optionalPayment = paymentRepository.findByCustomerAndGroupAndServiceAndStartedPeriodAndIsPayedFalse(
                c, group, service, startedDate);

            if (optionalPayment.isEmpty()) {
                return new ResponseDTO(OK, "Looks like you already paid", false, null);
            }

            Payment paymentInDataBase = optionalPayment.get();
            Double currentPaidMoney = paymentInDataBase.getPayedMoney() + payedMoney;
            Double paymentForPeriod = paymentInDataBase.getPaymentForPeriod();

            if (currentPaidMoney > paymentForPeriod) {
                return new ResponseDTO(OK, "More than enough money", false, null);
            }

            if (currentPaidMoney.equals(paymentForPeriod)){
                paymentRepository.paymentForCurrentPeriodAndSetPayedTrue(
                    payedMoney, c.getId(), group.getId(), service.getId(), startedDate
                );
            }else {
                paymentRepository.paymentForCurrentPeriod(
                    payedMoney, c.getId(), group.getId(), service.getId(), startedDate
                );
            }

//            paymentRepository.setPayedTrueIfCompletelyPaid(
//                c.getId(), group.getId(), service.getId(), startedDate
//            );

            subtractCustomerAccount(c, responseDTO, payedMoney);
            createPaymentHistory(service, c, payedMoney, group);
            return new ResponseDTO(OK, "Successfully payed for current period", true, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO(SERVER_ERROR, e.getMessage(), false, null);
        }
    }

    private void createPaymentHistory(Services service, Customers c, Double payedMoney, Groups group) {
        PaymentHistory paymentHistory = new PaymentHistory();
        Optional<GroupsDTO> optionalGroupsDTO = groupsService.findOne(group.getId());
        if (optionalGroupsDTO.isPresent()) {
            GroupsDTO groups = optionalGroupsDTO.get();
            paymentHistory.setGroupName(groups.getName());
            paymentHistory.setOrganizationName(groups.getGroupOwnerName());
        }
        paymentHistory.setServiceName(service.getServiceType().name());
        paymentHistory.setCustomer(c);
        paymentHistory.setSum(payedMoney);
        paymentHistory.setCreatedAt(LocalDate.now());
        paymentHistoryService.save(paymentHistory);
    }

    private void subtractCustomerAccount(Customers c, ResponseDTO<Double> responseDTO, Double payedMoney) {
        CustomersDTO customersDTO = new CustomersDTO();
        customersDTO.setId(c.getId());
        Optional<CustomersDTO> optionalCustomer = customersService.findOne(c.getId());

        if (optionalCustomer.isPresent()) {
            CustomersDTO customer = optionalCustomer.get();

            customersDTO.setUsername(customer.getUsername());
            customersDTO.setPassword(customer.getPassword());
            customersDTO.setPhoneNumber(customer.getPhoneNumber());
            customersDTO.setUser(customer.getUser());
        }
        Double customerAccount = responseDTO.getResponseData();
        customersDTO.setAccount(customerAccount - payedMoney);
        customersService.update(customersDTO);
    }

    private ResponseDTO<Double> checkCustomerAccount(PaymentDTO paymentDTO) {
        CustomersDTO customer = paymentDTO.getCustomer();
        CustomersDTO customerInDataBase = customersService.findByIdAccountGreaterThen(
            customer.getId(), paymentDTO.getPayedMoney()
        );
        if (customerInDataBase == null) {
            return new ResponseDTO<>(NOT_FOUND, ResponseMessage.NOT_FOUND, false, null);
        }
        Double customerAccount = customerInDataBase.getAccount();
        if (customerAccount < paymentDTO.getPayedMoney()) {
            return new ResponseDTO<>(NOT_ENOUGH, ResponseMessage.NOT_ENOUGH, false, null);
        }
        return new ResponseDTO<>(null, "", true, customerAccount);
    }

    public static void setFinishedPeriodToPayment(Payment payment, Services service) {
        Integer countPeriod = service.getCountPeriod();

        switch (service.getPeriodType()) {
            case DAY -> {
                // next finished period
                payment.setFinishedPeriod(payment.getStartedPeriod().plusDays(countPeriod));
            }
            case WEEK -> {
                payment.setFinishedPeriod(payment.getStartedPeriod().plusWeeks(countPeriod));
            }
            case MONTH -> {
                payment.setFinishedPeriod(payment.getStartedPeriod().plusMonths(countPeriod));
            }
            case YEAR -> {
                payment.setFinishedPeriod(payment.getStartedPeriod().plusYears(countPeriod));
            }
        }
    }

    @Override
    public Optional<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        log.debug("Request to partially update PaymentDTO : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .map(paymentRepository::save)
            .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Payments");
        return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> findOne(Long id) {
        log.debug("Request to get PaymentDTO : {}", id);
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PaymentDTO : {}", id);
        paymentRepository.deleteById(id);
    }
}
