package uz.devops.intern.schedule;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import uz.devops.intern.service.PaymentService;

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
