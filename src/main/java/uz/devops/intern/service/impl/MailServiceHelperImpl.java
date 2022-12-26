package uz.devops.intern.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.devops.intern.file.io.PDFWriterClass;
import uz.devops.intern.service.MailServiceHelper;
import uz.devops.intern.service.dto.PaymentDTO;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.MAIL_MESSAGE_NOTIFICATION_DEBT;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceHelperImpl implements MailServiceHelper {
    @Value("${spring.mail.username}")
    private String mailCPMSystem;
    private final JavaMailSender javaMailSender;
    private final PDFWriterClass PDFWriterClass;

    @Override
    public synchronized void invokerPdfWriterAndMailSender(String to, String subject, ResourceBundle resourceBundle, List<PaymentDTO> paymentDTOList){
        PDFWriterClass.convertToPDFFromExcel(paymentDTOList, resourceBundle);
        log.info("current thread while sending message to email {}" ,Thread.currentThread().getName());
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            File file = new File("src/main/resources/templates/customer_debts.pdf");
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,  StandardCharsets.UTF_8.name());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(mailCPMSystem);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setReplyTo(mailCPMSystem);
            mimeMessageHelper.setText(resourceBundle.getString(MAIL_MESSAGE_NOTIFICATION_DEBT), true);
            mimeMessageHelper.addAttachment("debts-info.pdf", file);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message;
        try {
            message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            message.setFrom(mailCPMSystem);
            message.setTo(to);
            message.setSubject("Debt for services");
            message.setText(text, false);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }
}
