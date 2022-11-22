package uz.devops.intern.schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Payment;
import uz.devops.intern.domain.Services;
import uz.devops.intern.repository.PaymentRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@Component
@EnableScheduling
public class TimerTaskToSendMessage {
    private final PaymentRepository paymentRepository;
    public TimerTaskToSendMessage(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    public void sendNotificationIfCustomerNotPaidForService(
        Customers customer, Groups group, Services service, LocalDate startedPeriod, LocalDate endPeriod){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Optional<Payment> paymentOptional = paymentRepository.findByCustomerAndGroupAndServiceAndStartedPeriodAndIsPayedFalse(
                    customer, group, service, startedPeriod
                );
                if (paymentOptional.isPresent()){
                    Payment payment = paymentOptional.get();
                    System.out.println("============= Qarzdorlik ==============");
                    System.out.println(payment);
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, startedPeriod.toEpochDay(), 5000);
    }
}
