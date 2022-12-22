package uz.devops.intern.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import uz.devops.intern.domain.*;
import uz.devops.intern.file.io.ConvertExcelToPDF;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.MailServiceHelper;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.dto.PaymentDTO;

import java.time.LocalDate;
import java.util.*;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
import static uz.devops.intern.constants.StringFormatConstants.STRING_FORMAT_MAIL_SEND_DESCRIPTION;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingCustomerTelegram;
import static uz.devops.intern.telegram.bot.utils.TelegramCustomerUtils.buildCustomerPayments;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TimerTaskToSendMessage {
    private final MailServiceHelper mailServiceHelper;
    private final PaymentService paymentService;
    private final CustomerTelegramService customerTelegramService;
    private final ConvertExcelToPDF excelToPDF;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("message", new Locale("uz"));

    public void sendNotificationIfCustomerNotPaidForService(
        Customers customer, Groups group, Services service, LocalDate startedPeriod, LocalDate endPeriod){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                List<PaymentDTO> paymentList = paymentService
                    .findAllByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(
                    customer,  service, group, startedPeriod);

                Optional<CustomerTelegram> optionalCustomerTelegram = customerTelegramService.findByCustomer(customer);
                optionalCustomerTelegram.ifPresent(customerTelegram -> resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram));

                if (paymentList != null){
                    log.info("customer of debts: " + paymentList);
                    String text = String.format(STRING_FORMAT_MAIL_SEND_DESCRIPTION, resourceBundle.getString(MAIL_SAY_HELLO_CUSTOMER), customer.getUsername(), resourceBundle.getString(MAIL_MESSAGE_DEBTS_DESCRIPTION), resourceBundle.getString(MAIL_MESSAGE_DEBTS));

                    excelToPDF.convertToPDFFromExcel(paymentList, resourceBundle);
                    mailServiceHelper.sendMessageWithPDF(customer.getUser().getEmail(), resourceBundle.getString(MAIL_MESSAGE_NOTIFICATION_DEBT), text);
                    log.info("Successfully send message to email customer");
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, startedPeriod.toEpochDay(), 1000 * 60);
    }
}
