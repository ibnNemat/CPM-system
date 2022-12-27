package uz.devops.intern.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import uz.devops.intern.domain.*;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.MailServiceHelper;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.dto.PaymentDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
import static uz.devops.intern.constants.StringFormatConstants.STRING_FORMAT_MAIL_SEND_DESCRIPTION;
import static uz.devops.intern.constants.StringFormatConstants.STRING_FORMAT_MAIL_SEND_DESCRIPTION_VERSION2;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingCustomerTelegram;
import static uz.devops.intern.telegram.bot.utils.TelegramCustomerUtils.buildCustomerPaymentsToSendEmailMessage;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TimerTaskToSendMessage {
    private final MailServiceHelper mailServiceHelper;
    private final PaymentService paymentService;
    private final CustomerTelegramService customerTelegramService;
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("message", new Locale("uz"));

    public void sendNotificationIfCustomerNotPaidForService(
        Customers customer, Groups group, Services service, LocalDate startedPeriod, LocalDate endPeriod){

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                List<PaymentDTO> paymentList = paymentService
                    .findAllByCustomerAndGroupAndServiceAndStartedPeriodAndIsPaidFalse(customer,  service, group, startedPeriod);
                Optional<CustomerTelegram> optionalCustomerTelegram = customerTelegramService.findByCustomer(customer);
                optionalCustomerTelegram.ifPresent(customerTelegram -> resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram));

                if (paymentList != null){
                    log.info("customer of debts: " + paymentList);
                    StringBuilder buildCustomerDebtsList = new StringBuilder();
                    for (PaymentDTO paymentDTO : paymentList) {
                        buildCustomerPaymentsToSendEmailMessage(paymentDTO, buildCustomerDebtsList, resourceBundle.getLocale().getLanguage());
                    }

                    String text = String.format(STRING_FORMAT_MAIL_SEND_DESCRIPTION, resourceBundle.getString(MAIL_SAY_HELLO_CUSTOMER), customer.getUsername(), resourceBundle.getString(MAIL_MESSAGE_DEBTS_DESCRIPTION), resourceBundle.getString(MAIL_MESSAGE_DEBTS));
                    try {
                        mailServiceHelper.invokerPdfWriterAndMailSender(customer.getUser().getEmail(), text, resourceBundle, paymentList);
                        log.info("Successfully send message with pdf file to email customer");
                    }catch (NotOfficeXmlFileException e){
                        text = String.format(STRING_FORMAT_MAIL_SEND_DESCRIPTION_VERSION2, resourceBundle.getString(MAIL_SAY_HELLO_CUSTOMER), customer.getUsername(), resourceBundle.getString(MAIL_MESSAGE_DEBTS_DESCRIPTION), resourceBundle.getString(MAIL_MESSAGE_DEBTS), buildCustomerDebtsList.toString());
                        mailServiceHelper.sendSimpleMessage(customer.getUser().getEmail(), resourceBundle.getString(MAIL_MESSAGE_NOTIFICATION_DEBT), text);
                        log.info("Successfully send simple message to email customer");
                    }
                }

                timer.cancel();
                timer.purge();
            }
        };
        Date startedDate = Date.from(startedPeriod.atStartOfDay(ZoneId.systemDefault()).toInstant());
        timer.schedule(timerTask, startedDate, endPeriod.toEpochDay());
    }
}
