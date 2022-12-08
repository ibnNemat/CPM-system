package uz.devops.intern.service;

import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.User;

public interface AdminTgService {

    void main(Update update);
    void pressedStartCommand(Message message);

    Boolean getLanguage(Message message, CustomerTelegram customer);

    Boolean verifyAdminByPhoneNumber(Message message, CustomerTelegram customer);

    Boolean getAdminBotToken(Message message, CustomerTelegram customer);

    void checkIsBotInGroup(ChatMember user, Chat chat, String botId);

//    void checkIsBotAdmin(ChatMemberUpdated member);

    boolean menu(Message message, CustomerTelegram customer);

    Boolean addOrganization(Message message, CustomerTelegram manager);

    Boolean addGroup(CallbackQuery callbackQuery, CustomerTelegram manager);

    void addServices(Message message, CustomerTelegram manager);
}
