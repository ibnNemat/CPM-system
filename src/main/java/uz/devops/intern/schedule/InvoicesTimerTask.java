package uz.devops.intern.schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class InvoicesTimerTask {
//    private final PaymentService paymentService;
//
//    public InvoicesTimerTask(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }

    public void sendNotificationIfCustomerNotPaidForService(String username){

    }
}
