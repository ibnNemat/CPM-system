package uz.devops.intern.service;

import uz.devops.intern.service.dto.PaymentDTO;

import java.util.List;
import java.util.ResourceBundle;

public interface MailServiceHelper {
    void sendSimpleMessage(String to, String subject, String text);
    void invokerPdfWriterAndMailSender(String to, String subject, ResourceBundle resourceBundle, List<PaymentDTO> paymentDTOList);

}
