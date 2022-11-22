package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.Services;
import uz.devops.intern.repository.ServicesRepository;
import uz.devops.intern.schedule.TimerTaskToSendMessage;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.ServicesService;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ServicesDTO;
import uz.devops.intern.service.mapper.ServiceMapper;
import uz.devops.intern.service.mapper.ServicesMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Services}.
 */
@Service
public class ServicesServiceImpl implements ServicesService {
    private final Logger log = LoggerFactory.getLogger(ServicesServiceImpl.class);
    private final ServicesRepository servicesRepository;
    private final ServicesMapper servicesMapper;
    private final PaymentService paymentService;
    private final TimerTaskToSendMessage taskToSendMessage;

    public ServicesServiceImpl(ServicesRepository servicesRepository, ServicesMapper servicesMapper, PaymentService paymentService, TimerTaskToSendMessage taskToSendMessage) {
        this.servicesRepository = servicesRepository;
        this.servicesMapper = servicesMapper;
        this.paymentService = paymentService;
        this.taskToSendMessage = taskToSendMessage;
    }

    @Override
    public ServicesDTO save(ServicesDTO servicesDTO) {
        try {
            log.debug("Request to save Services : {}", servicesDTO);
            Services services = ServiceMapper.toEntity(servicesDTO);
            System.out.println(services);
            services = servicesRepository.save(services);

            createPaymentEachCustomers(services);
            return ServiceMapper.toDtoForSaveServiceMethod(services);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void createPaymentEachCustomers(Services service) {
        Payment p = new Payment();
        LocalDate startedPeriod = service.getStartedPeriod();
        p.setStartedPeriod(startedPeriod);
        PaymentServiceImpl.setFinishedPeriodToPayment(p, service);
        LocalDate endPeriod = p.getFinishedPeriod();

        Set<Customers> customersSet = service.getCustomers();
        Groups g = service.getGroup();
        List<Payment> paymentList = new ArrayList<>();
        for(Customers c: customersSet){
            Payment payment = new Payment();
            payment.setService(service);
            payment.setCustomer(c);
            payment.setGroup(g);
            payment.setStartedPeriod(startedPeriod);
            payment.setFinishedPeriod(endPeriod);
            payment.setIsPayed(false);
            payment.setPayedMoney(0D);
            payment.setPaymentForPeriod(service.getPrice());
            paymentList.add(payment);
            taskToSendMessage.sendNotificationIfCustomerNotPaidForService(
                c, g, service, startedPeriod, endPeriod
            );
        }
        paymentService.saveAll(paymentList);
    }

    @Override
    public ServicesDTO update(ServicesDTO servicesDTO) {
        log.debug("Request to update Services : {}", servicesDTO);
        Services services = ServiceMapper.toEntity(servicesDTO);

        services = servicesRepository.save(services);
        return ServiceMapper.toDtoForSaveServiceMethod(services);
    }

    @Override
    public Optional<ServicesDTO> partialUpdate(ServicesDTO servicesDTO) {
        log.debug("Request to partially update Services : {}", servicesDTO);

        return servicesRepository
            .findById(servicesDTO.getId())
            .map(existingServices -> {
                servicesMapper.partialUpdate(existingServices, servicesDTO);

                return existingServices;
            })
            .map(servicesRepository::save)
            .map(ServiceMapper::toDtoForGetting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicesDTO> findAll() {
        log.debug("Request to get all Services");
        return servicesRepository.findAll().stream()
            .map(ServiceMapper::toDtoForGetting)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServicesDTO> findOne(Long id) {
        log.debug("Request to get Services : {}", id);
        return servicesRepository.findById(id).map(ServiceMapper::toDtoForGetting);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Services : {}", id);
        servicesRepository.deleteById(id);
    }
}
