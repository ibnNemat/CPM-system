package uz.devops.intern.service;

import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.User;

public interface AdminTgService {

    boolean main(Update update);
//    void pressedStartCommand(Message message);

}
