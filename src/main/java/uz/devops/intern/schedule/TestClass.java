package uz.devops.intern.schedule;

import org.springframework.stereotype.Component;
import uz.devops.intern.domain.Customers;
import uz.devops.intern.domain.Groups;
import uz.devops.intern.domain.Services;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
public class TestClass {
    private final TimerTaskToSendMessage timerTaskToSendMessage;

    public TestClass(TimerTaskToSendMessage timerTaskToSendMessage) {
        this.timerTaskToSendMessage = timerTaskToSendMessage;
    }

    @PostConstruct
    public void test(){
        Customers customers = new Customers(); customers.setId(2L);
        Groups groups = new Groups(); groups.setId(10901L);
        Services services = new Services(); services.setId(10955L);
        LocalDate startedPeriod = LocalDate.of(2022, 12, 1);
        LocalDate endPeriod = LocalDate.of(2022, 12, 2);
        timerTaskToSendMessage.sendNotificationIfCustomerNotPaidForService(
            customers, groups, services, startedPeriod,endPeriod
        );
    }
}
