package uz.devops.intern.schedule;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import uz.devops.intern.domain.*;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.dto.PaymentDTO;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
import static uz.devops.intern.constants.StringFormatConstants.STRING_FORMAT_MAIL_SEND_DESCRIPTION;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingCustomerTelegram;
import static uz.devops.intern.telegram.bot.utils.TelegramCustomerUtils.buildCustomerPayments;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class TimerTaskToSendMessage {
    @Value("${spring.mail.username}")
    private String mailCPMSystem;
    @Autowired
    private JavaMailSender javaMailSender;
    private final PaymentService paymentService;
    private final CustomerTelegramService customerTelegramService;
    private final Logger log = LoggerFactory.getLogger(TimerTaskToSendMessage.class);
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
                    StringBuilder buildCustomerDebtsList = new StringBuilder();
                    for (PaymentDTO paymentDTO : paymentList) {
                        buildCustomerPayments(paymentDTO, buildCustomerDebtsList, resourceBundle.getLocale().getLanguage());
                    }

                    log.info("customer of debts: " + paymentList);
                    String text = String.format(STRING_FORMAT_MAIL_SEND_DESCRIPTION, resourceBundle.getString(MAIL_SAY_HELLO_CUSTOMER), customer.getUsername(), resourceBundle.getString(MAIL_MESSAGE_DEBTS_DESCRIPTION), resourceBundle.getString(MAIL_MESSAGE_DEBTS), buildCustomerDebtsList.toString());
                    sendSimpleMessage(customer.getUser().getEmail(), "", text);

//                    sendMessage();
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, startedPeriod.toEpochDay(), 1000 * 60);
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = null;
        try {
            message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            message.setFrom(mailCPMSystem);
            message.setTo(to);
            message.setSubject("Debt for services");
            message.setText(text, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
