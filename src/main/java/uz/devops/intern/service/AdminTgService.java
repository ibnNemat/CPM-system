package uz.devops.intern.service;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface AdminTgService {

    void pressedStartCommand(Message message);

    void verifyAdminByPhoneNumber(Message message);

    void getAdminBotToken(Message message);
}
