package uz.devops.intern.schedule;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ScheduleJob {
    private final CustomerTelegramService customerTelegramService;
    private final Logger log = LoggerFactory.getLogger(ScheduleJob.class);

//    @Scheduled(fixedDelay = 1000)
    public void checkCustomerProfileIsActive() throws InterruptedException {
        log.info("Schedule started working");
        List<CustomerTelegramDTO> customerTelegramDTOS = customerTelegramService.findAll();

        if (customerTelegramDTOS != null){
            List<Long> telegramCustomerIds = new ArrayList<>();

            customerTelegramDTOS.forEach(
                (telegramCustomer) -> telegramCustomerIds.add(telegramCustomer.getId())
            );

            // set false all telegram customers profile to check isActive field
            customerTelegramService.setFalseToTelegramCustomerProfile(telegramCustomerIds);

            timerTaskToDeleteNotActivatingTelegramCustomers(telegramCustomerIds);
            Thread.sleep(1000 * 60 * 15);
        }
    }

    @Transactional
    public void timerTaskToDeleteNotActivatingTelegramCustomers(List<Long> telegramCustomerIds){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                log.info("Timer task started working");
                customerTelegramService.deleteAllTelegramCustomersIsActiveFalse(telegramCustomerIds);
                timer.cancel();
                timer.purge();
            }
        };

        timer.schedule(timerTask, 1000 * 60 * 15);
    }
}
