package uz.devops.intern.service;

public interface MailServiceHelper {
    void sendSimpleMessage(String to, String subject, String text);
    void sendMessageWithPDF(String to, String subject, String text);
}
