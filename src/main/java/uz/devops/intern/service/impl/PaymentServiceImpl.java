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
import uz.devops.intern.service.mapper.PaymentsMapper;
import uz.devops.intern.service.utils.AuthenticatedUserUtil;
import uz.devops.intern.service.utils.ContextHolderUtil;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final CustomersService customersService;
    private final ServicesRepository servicesRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final GroupsService groupsService;
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final PaymentMapper paymentMapper;
    private static final String ENTITY_NAME = "payment";
    public PaymentServiceImpl(PaymentRepository paymentRepository, CustomersService customersService, ServicesRepository servicesRepository, PaymentHistoryService paymentHistoryService, GroupsService groupsService, AuthenticatedUserUtil authenticatedUserUtil, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.customersService = customersService;
        this.servicesRepository = servicesRepository;
        this.paymentHistoryService = paymentHistoryService;
        this.groupsService = groupsService;
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentDTO save(PaymentDTO paymentDTO) {
        log.debug("Request to save PaymentDTO : {}", paymentDTO);
        uz.devops.intern.domain.Payment payment = PaymentsMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return PaymentsMapper.toDto(payment);
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
        if (paymentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (!paymentRepository.existsById(paymentDTO.getId())) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        uz.devops.intern.domain.Payment payment = PaymentsMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return PaymentsMapper.toDto(payment);
    }
    @Override
    public ResponseDTO payForService(PaymentDTO paymentDTO) {
        log.debug("Request to pay for Service : {}", paymentDTO);

        Payment requestPayment = PaymentsMapper.toEntity(paymentDTO);
        Groups group = requestPayment.getGroup();
        Customers customerPayer = authenticatedUserUtil.getAuthenticatedUser();

        Services service = requestPayment.getService();
        Optional<Services> servicesOptional = servicesRepository.findById(service.getId());

        if (servicesOptional.isEmpty() || customerPayer == null) {
            return new ResponseDTO(NOT_FOUND, ResponseMessage.NOT_FOUND, false, null);
        }
        requestPayment.setCustomer(customerPayer);
        ResponseDTO<Double> responseDTO = checkCustomerBalance(requestPayment);
        if (!responseDTO.getSuccess()) {
            return responseDTO;
        }

        service = servicesOptional.get();
        LocalDate startedDate = requestPayment.getStartedPeriod();
        Double requestPaidMoney = requestPayment.getPaidMoney();

        Optional<Payment> optionalPayment = paymentRepository
            .findByCustomerAndGroupAndServiceAndStartedPeriodAndIsPayedFalse(
            customerPayer, group, service, startedDate);
        if (optionalPayment.isEmpty()) {
            return new ResponseDTO(OK, "Looks like you already paid", false, null);
        }

        Payment paymentInDataBase = optionalPayment.get();
        Double paymentForPeriod = paymentInDataBase.getPaymentForPeriod();
        if (paymentInDataBase.getPaidMoney() + requestPaidMoney >= paymentForPeriod) {
            Double owedMoney = paymentForPeriod - paymentInDataBase.getPaidMoney();
            requestPaidMoney -= owedMoney;

            List<Payment> paymentList = new ArrayList<>();
            paymentInDataBase.setPaidMoney(paymentForPeriod);
            paymentInDataBase.setIsPayed(true);
            paymentList.add(paymentInDataBase);

            LocalDate oldFinishedPeriodDateWithPlusDate = paymentInDataBase.getFinishedPeriod().plusDays(1);
            Payment newPayment = new Payment();
            boolean bool = false;
            while(requestPaidMoney >= paymentForPeriod){
                newPayment = buildNewPayment(service, paymentForPeriod, group, customerPayer, oldFinishedPeriodDateWithPlusDate);
                requestPaidMoney -= paymentForPeriod;
                paymentList.add(newPayment);
                oldFinishedPeriodDateWithPlusDate = newPayment.getFinishedPeriod().plusDays(1);
                bool = true;
            }

            if (requestPaidMoney != 0){
                if (bool){
                    oldFinishedPeriodDateWithPlusDate = newPayment.getFinishedPeriod().plusDays(1);
                }
                newPayment = buildNewPayment(service, paymentForPeriod, group, customerPayer, oldFinishedPeriodDateWithPlusDate);
                newPayment.setIsPayed(false);
                newPayment.setPaidMoney(requestPaidMoney);
                paymentList.add(newPayment);
            }

            paymentRepository.saveAll(paymentList);
            customersService.decreaseCustomerBalance(requestPaidMoney, customerPayer.getId());
            createPaymentHistory(service, customerPayer, group, requestPaidMoney);
            return new ResponseDTO(OK, "Successfully payed", true, null);
        }

        paymentInDataBase.setPaidMoney(paymentInDataBase.getPaidMoney()+requestPaidMoney);
        customersService.decreaseCustomerBalance(requestPaidMoney, customerPayer.getId());
        createPaymentHistory(service, customerPayer, group, requestPaidMoney);

        return new ResponseDTO(OK, "Successfully payed", true, null);
    }

    private Payment buildNewPayment(Services service, Double paymentForPeriod, Groups group, Customers customerPayer, LocalDate oldFinishedDate){
        Payment paymentToSetPeriod = new Payment();
        paymentToSetPeriod.setStartedPeriod(oldFinishedDate);
        setNextFinishedPeriodToPayment(paymentToSetPeriod, service);
        Payment newPayment = new Payment();
        newPayment.setGroup(group);
        newPayment.setPaymentForPeriod(paymentForPeriod);
        newPayment.setPaidMoney(paymentForPeriod);
        newPayment.setIsPayed(true);
        newPayment.setService(service);
        newPayment.setCustomer(customerPayer);
        newPayment.setStartedPeriod(paymentToSetPeriod.getStartedPeriod());
        newPayment.setFinishedPeriod(paymentToSetPeriod.getFinishedPeriod());
        return newPayment;
    }

    private void createPaymentHistory(Services service, Customers customer, Groups group, Double paidMoney) {
        PaymentHistory paymentHistory = new PaymentHistory();
        Optional<GroupsDTO> optionalGroupsDTO = groupsService.findOne(group.getId());
        if (optionalGroupsDTO.isPresent()) {
            GroupsDTO groups = optionalGroupsDTO.get();
            paymentHistory.setGroupName(groups.getName());
            paymentHistory.setOrganizationName(groups.getGroupOwnerName());
        }
        paymentHistory.setServiceName(service.getName());
        paymentHistory.setCustomer(customer);
        paymentHistory.setSum(paidMoney);
        paymentHistory.setCreatedAt(LocalDate.now());
        paymentHistoryService.save(paymentHistory);
    }

    private ResponseDTO<Double> checkCustomerBalance(Payment payment) {
        Customers customer = payment.getCustomer();
        Optional<CustomersDTO> customerInDataBaseOptional = customersService.findOne(customer.getId());

        if (customerInDataBaseOptional.isEmpty()) {
            return new ResponseDTO<>(NOT_FOUND, ResponseMessage.NOT_FOUND, false, null);
        }

        CustomersDTO customerInDataBase = customerInDataBaseOptional.get();
        Double customerAccount = customerInDataBase.getBalance();
        if (customerAccount < payment.getPaidMoney()) {
            return new ResponseDTO<>(NOT_ENOUGH, ResponseMessage.NOT_ENOUGH, false, null);
        }
        return new ResponseDTO<>(null, "", true, customerAccount);
    }

    public static void setNextFinishedPeriodToPayment(Payment payment, Services service) {
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
            .map(PaymentsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Payments");
        return paymentRepository.findAll(pageable).map(PaymentsMapper::toDto);
    }

    @Override
    public ResponseDTO<List<PaymentDTO>> getAllCustomerPayments() {
        log.debug("Request to get all customer Payments");
        Customers customer = authenticatedUserUtil.getAuthenticatedUser();
        if (customer == null)
            return new ResponseDTO<>(NOT_FOUND, "customer not found", false, null);

        List<Payment> customerPayments = paymentRepository.findAllByCustomer(customer);
        if (customerPayments.size() == 0)
            return new ResponseDTO<>(NOT_FOUND, "customer payments not found", false, null);
        List<PaymentDTO> paymentDTOList = customerPayments.stream()
            .map(PaymentsMapper::toDto)
            .toList();
        return new ResponseDTO<>(OK, ResponseMessage.OK, true, paymentDTOList);
    }

    @Override
    public List<PaymentDTO> getAllPaymentsCreatedByGroupManager(){
        String managerName = ContextHolderUtil.getUsernameFromContextHolder();
        List<Payment> paymentList = paymentRepository.findAllByGroupOwnerName(managerName);
        return PaymentsMapper.paymentDTOSet(paymentList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> findOne(Long id) {
        log.debug("Request to get PaymentDTO : {}", id);
        return paymentRepository.findById(id).map(PaymentsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PaymentDTO : {}", id);
        paymentRepository.deleteById(id);
    }
}
