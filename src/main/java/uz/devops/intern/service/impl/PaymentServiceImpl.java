package uz.devops.intern.service.impl;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.constants.ResponseMessageConstants;
import uz.devops.intern.domain.*;
import uz.devops.intern.repository.PaymentRepository;
import uz.devops.intern.repository.ServicesRepository;
import uz.devops.intern.service.CustomersService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.PaymentHistoryService;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.mapper.*;
import uz.devops.intern.service.utils.AuthenticatedUserUtil;
import uz.devops.intern.service.utils.ContextHolderUtil;
import uz.devops.intern.web.rest.errors.BadRequestAlertException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static uz.devops.intern.constants.ResponseCodeConstants.*;

/**
 * Service Implementation for managing {@link uz.devops.intern.domain.Payment}.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final CustomersService customersService;
    private final ServicesRepository servicesRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final GroupsService groupsService;
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final PaymentMapper paymentMapper;
    private final CustomersMapper customersMapper;
    private final PaymentHistoryMapper paymentHistoryMapper;
    private static final String ENTITY_NAME = "payment";

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
    public ResponseDTO<PaymentHistoryDTO> payForService(PaymentDTO paymentDTO) {
        log.debug("Request to pay for Service : {}", paymentDTO);
        Payment requestPayment = PaymentsMapper.toEntity(paymentDTO);
        Groups group = requestPayment.getGroup();
        Customers customerPayer = authenticatedUserUtil.getAuthenticatedUser();

        Services service = requestPayment.getService();
        Optional<Services> servicesOptional = servicesRepository.findById(service.getId());

        if (servicesOptional.isEmpty() || customerPayer == null) {
            return new ResponseDTO<>(NOT_FOUND, ResponseMessageConstants.NOT_FOUND, false, null);
        }
        service = servicesOptional.get();
        LocalDate startedDate = requestPayment.getStartedPeriod();
        if (!paymentRepository.existsByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(
            customerPayer, group, service, startedDate)){
            return new ResponseDTO<>(NOT_FOUND, "customer payment not found", false, null);
        }

        requestPayment.setCustomer(customerPayer);
        ResponseDTO<Double> responseDTO = checkCustomerBalance(requestPayment);
        if (!responseDTO.getSuccess()) {
            return new ResponseDTO<>(responseDTO.getCode(), responseDTO.getMessage(), false, null);
        }

        Double requestPaidMoney = requestPayment.getPaidMoney();
        List<Payment> customerPayments = paymentRepository.findAllByCustomerAndGroupAndServiceAndIsPaidFalseOrderByStartedPeriod(customerPayer, group, service);

        if (!customerPayments.get(0).getStartedPeriod().equals(service.getStartedPeriod())){
            return new ResponseDTO<>(NOT_FOUND, "The customer must pay for the old service", false, null);
        }

        AtomicDouble atomicDouble = new AtomicDouble();
        customerPayments.forEach(payment -> atomicDouble.addAndGet(payment.getPaymentForPeriod() - payment.getPaidMoney()));
        Double sumAllPayments = atomicDouble.get();
        Payment currenPayment = customerPayments.stream().filter(payment -> payment.getStartedPeriod().equals(startedDate)).findAny().orElseThrow();
        Double paymentForPeriod = currenPayment.getPaymentForPeriod();

        if (requestPaidMoney > sumAllPayments){
            return new ResponseDTO<>(MORE_THEN_ENOUGH, "Entered too much money", false, null);
        }

        if (currenPayment.getPaidMoney() + requestPaidMoney >= paymentForPeriod) {
            double owedMoney = paymentForPeriod - currenPayment.getPaidMoney();
            requestPaidMoney -= owedMoney;

            currenPayment.setPaidMoney(currenPayment.getPaymentForPeriod());
            currenPayment.setIsPaid(true);

            int indexPayment = 1;
            while(requestPaidMoney >= paymentForPeriod && indexPayment < customerPayments.size()){
                customerPayments.get(indexPayment).setPaidMoney(paymentForPeriod);
                customerPayments.get(indexPayment).setIsPaid(true);
                requestPaidMoney -= paymentForPeriod;
                indexPayment++;
            }
            if (requestPaidMoney != 0){
                customerPayments.get(indexPayment).setPaidMoney(requestPaidMoney);
            }
            customersService.decreaseCustomerBalance(requestPaidMoney, customerPayer.getId());
            PaymentHistory paymentHistory = createPaymentHistory(service, customerPayer, group, requestPayment.getPaidMoney());
            return new ResponseDTO<>(OK, "Successfully payed", true, paymentHistoryMapper.toDto(paymentHistory));
        }

        currenPayment.setPaidMoney(currenPayment.getPaidMoney()+requestPaidMoney);
        customersService.decreaseCustomerBalance(requestPaidMoney, customerPayer.getId());
        PaymentHistory paymentHistory = createPaymentHistory(service, customerPayer, group, requestPayment.getPaidMoney());

        return new ResponseDTO<>(OK, "Successfully payed", true, paymentHistoryMapper.toDto(paymentHistory));
    }

    private Payment buildNewPayment(Services service, Double paymentForPeriod, Groups group, Customers customerPayer, LocalDate oldFinishedDate){
        Payment paymentToSetPeriod = new Payment();
        paymentToSetPeriod.setStartedPeriod(oldFinishedDate);
        setNextFinishedPeriodToPayment(paymentToSetPeriod, service);
        Payment newPayment = new Payment();
        newPayment.setGroup(group);
        newPayment.setPaymentForPeriod(paymentForPeriod);
        newPayment.setPaidMoney(paymentForPeriod);
        newPayment.setIsPaid(true);
        newPayment.setService(service);
        newPayment.setCustomer(customerPayer);
        newPayment.setStartedPeriod(paymentToSetPeriod.getStartedPeriod());
        newPayment.setFinishedPeriod(paymentToSetPeriod.getFinishedPeriod());
        return newPayment;
    }

    private PaymentHistory createPaymentHistory(Services service, Customers customer, Groups group, Double paidMoney) {
        PaymentHistory paymentHistory = new PaymentHistory();
        Optional<GroupsDTO> optionalGroupsDTO = groupsService.findOne(group.getId());
        if (optionalGroupsDTO.isPresent()) {
            GroupsDTO groups = optionalGroupsDTO.get();
            paymentHistory.setGroupName(groups.getName());
            paymentHistory.setOrganizationName(groups.getOrganization().getName());
        }
        paymentHistory.setServiceName(service.getName());
        paymentHistory.setCustomer(customer);
        paymentHistory.setSum(paidMoney);
        paymentHistory.setCreatedAt(LocalDate.now());
        return paymentHistoryService.save(paymentHistory);
    }

    private ResponseDTO<Double> checkCustomerBalance(Payment payment) {
        Customers customer = payment.getCustomer();
        Optional<CustomersDTO> customerInDataBaseOptional = customersService.findOne(customer.getId());

        if (customerInDataBaseOptional.isEmpty()) {
            return new ResponseDTO<>(NOT_FOUND, ResponseMessageConstants.NOT_FOUND, false, null);
        }

        CustomersDTO customerInDataBase = customerInDataBaseOptional.get();
        Double customerAccount = customerInDataBase.getBalance();
        if (customerAccount < payment.getPaidMoney()) {
            return new ResponseDTO<>(NOT_ENOUGH, ResponseMessageConstants.NOT_ENOUGH, false, null);
        }
        return new ResponseDTO<>(null, "", true, customerAccount);
    }

    public static void setNextFinishedPeriodToPayment(Payment payment, Services service) {
        Integer countPeriod = service.getCountPeriod();

        switch (service.getPeriodType()) {
            // next finished period
            case DAY -> payment.setFinishedPeriod(payment.getStartedPeriod().plusDays(countPeriod));
            case WEEK -> payment.setFinishedPeriod(payment.getStartedPeriod().plusWeeks(countPeriod));
            case MONTH -> payment.setFinishedPeriod(payment.getStartedPeriod().plusMonths(countPeriod));
            case YEAR -> payment.setFinishedPeriod(payment.getStartedPeriod().plusYears(countPeriod));
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
    public ResponseDTO<PaymentDTO> includePaymentToNewCustomer(PaymentRequestParamDTO paymentRequestParam) {
        Optional<CustomersDTO> customersOptional = customersService.findOne(paymentRequestParam.getCustomerId());
        if (customersOptional.isEmpty()) return ResponseDTO.<PaymentDTO>builder().code(NOT_FOUND).success(false).message("customer not found").build();
        Optional<Groups> groupsOptional = groupsService.findById(paymentRequestParam.getServiceId());
        if (groupsOptional.isEmpty()) return ResponseDTO.<PaymentDTO>builder().code(NOT_FOUND).success(false).message("group not found").build();
        Optional<Services> servicesOptional = servicesRepository.findById(paymentRequestParam.getServiceId());
        if (servicesOptional.isEmpty()) return ResponseDTO.<PaymentDTO>builder().code(NOT_FOUND).success(false).message("service not found").build();

        Customers customer = customersOptional.map(customersMapper::toEntity).get();
        Groups group = groupsOptional.get();
        Services service = servicesOptional.get();

        Payment paymentEntity = Payment.builder()
            .customer(customer)
            .isPaid(false)
            .service(service)
            .group(group)
            .paymentForPeriod(service.getPrice())
            .paidMoney(0D)
            .startedPeriod(service.getStartedPeriod())
            .build();

        setNextFinishedPeriodToPayment(paymentEntity, service);
        PaymentDTO paymentDTO = PaymentsMapper.toDto(paymentEntity);

        return new ResponseDTO<PaymentDTO>(OK, ResponseMessageConstants.SAVED, true, paymentDTO);
    }

    @Override
    public List<PaymentDTO> findAllByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(Customers customers, Services service, Groups group, LocalDate startedPeriod) {
        log.debug("Request to get all customer Payments paid is false");
        List<Payment> paymentList = paymentRepository.findAllByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(customers, group, service, startedPeriod);
        if (paymentList.size() == 0) return null;

        return PaymentsMapper.paymentDTOList(paymentList);
    }

    @Override
    public List<PaymentDTO> getAllCustomerPaymentsPayedIsFalse(Customers customer) {
        log.debug("Request to get all customer Payments which payment is false");
        List<Payment> paymentList = paymentRepository.findAllByCustomerAndIsPaidFalseOrderByStartedPeriod(customer);
        return PaymentsMapper.paymentDTOList(paymentList);
    }

    @Override
    public ResponseDTO<List<PaymentDTO>> getAllCustomerPayments() {
        log.debug("Request to get all customer Payments");
        Customers customer = authenticatedUserUtil.getAuthenticatedUser();
        if (customer == null)
            return new ResponseDTO<>(NOT_FOUND, "customer not found", false, null);

        List<Payment> customerPayments = paymentRepository.findAllByCustomerOrderByStartedPeriod(customer);
        if (customerPayments.size() == 0)
            return new ResponseDTO<>(NOT_FOUND, "customer payments not found", false, null);
        List<PaymentDTO> paymentDTOList = customerPayments.stream()
            .map(PaymentsMapper::toDto)
            .toList();
        return new ResponseDTO<>(OK, ResponseMessageConstants.OK, true, paymentDTOList);
    }

    @Override
    public List<PaymentDTO> getAllPaymentsCreatedByGroupManager(){
        log.debug("Request to get all payments created by group manager");
        String managerName = ContextHolderUtil.getUsernameFromContextHolder();
        List<Payment> paymentList = paymentRepository.findAllByGroupOwnerName(managerName);
        return PaymentsMapper.paymentDTOList(paymentList);
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

    @Override
    public ResponseDTO<PaymentDTO> getByCustomerId(Long customerId) {
        if(customerId == null){
            return ResponseDTO.<PaymentDTO>builder()
                .success(false).message("Parameter \"Customer id\" is null!").build();
        }

        Optional<Payment> paymentOptional = paymentRepository.findByCustomerId(customerId);
        if(paymentOptional.isEmpty()){
            return ResponseDTO.<PaymentDTO>builder()
                .success(false).message("Data is not found!").build();
        }

        PaymentDTO dto = paymentOptional.map(PaymentsMapper::toDto).get();
        return ResponseDTO.<PaymentDTO>builder()
            .success(true).message("OK").responseData(dto).build();
    }

    @Override
    public ResponseDTO<PaymentDTO> getByUserLogin(String login){
        if(login == null || login.trim().isEmpty()){
            return ResponseDTO.<PaymentDTO>builder()
                .success(false).message("Parameter \"Login\" is null or empty!").build();
        }

        Optional<Payment> paymentOptional = paymentRepository.findByUserLogin(login);
        if(paymentOptional.isEmpty()){
            return ResponseDTO.<PaymentDTO>builder()
                .success(false).message("Data is not found!").build();
        }

        PaymentDTO dto = paymentOptional.map(PaymentsMapper::toDto).get();
        return ResponseDTO.<PaymentDTO>builder()
            .success(true).message("OK").responseData(dto).build();

    }
}
