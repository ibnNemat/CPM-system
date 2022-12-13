package uz.devops.intern.telegram.bot.service.register;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.BotToken;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.BotTokenService;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.dto.BotTokenDTO;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.service.CommandHalfImpl;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BotAddGroup extends CommandHalfImpl {

    private final String STATE = "MANAGER_BOT_ADD_GROUP";
    private final Integer STEP = 0;

    private final BotTokenService botTokenService;
    private final TelegramGroupService telegramGroupService;
    private final CustomerTelegramService customerTelegramService;
    private final BotTokenRepository botTokenRepository;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        return false;
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    public void execute(Update update, String botId){
        boolean hasMyChatMember = update.hasMyChatMember();
        if (hasMyChatMember){
            Chat chat = update.getMyChatMember().getChat();
            User user = update.getMyChatMember().getFrom();
            if (!chat.getId().equals(user.getId())) {
                checkIsBotInGroup(update.getMyChatMember().getNewChatMember(), update.getMyChatMember().getChat(), botId);
            }
        }
    }

    public boolean checkIsBotInGroup(ChatMember user, Chat chat, String botId) {
        boolean isBot = user.getUser().getIsBot();
        if(isBot){
            log.warn("New user is not bot! Bot id: {} | New user: {}", botId, user);
            return false;
        }

        ResponseDTO<BotTokenDTO> responseDTO = botTokenService.findByChatId(Long.parseLong(botId), true);
        if(!responseDTO.getSuccess() && responseDTO.getResponseData() == null){
            log.warn("Bot is not found! Chat id: {} | Response: {}", botId, responseDTO);
            return false;
        }

        BotTokenDTO botToken = responseDTO.getResponseData();
        ResponseDTO<CustomerTelegramDTO> customerTgResponse = customerTelegramService.findByBotTgId(Long.parseLong(botId));
        if(!customerTgResponse.getSuccess() && customerTgResponse.getResponseData() == null){
            log.warn("Owner of bot is not found! BotTokenDTO: {}", botToken);
            return false;
        }
        CustomerTelegramDTO manager = customerTgResponse.getResponseData();

        boolean isNotExistsInGroups = isNotExistsInGroups(manager.getTelegramGroups(), botToken);
        if(!isNotExistsInGroups) {
            wrongValue(manager.getTelegramId(), "Bot guruhda mavjud!");
            return false;
        }

        User bot = user.getUser();
        sayThanksToManager(bot.getId(), manager);
        sendInviteLink(bot, chat.getId());
        saveToTelegramGroup(chat, manager);

        return true;
    }

    private boolean isNotExistsInGroups(Set<TelegramGroupDTO> telegramGroups, BotTokenDTO botToken){
        for(TelegramGroupDTO group: telegramGroups){
            URI uri = createCustomerURI(botToken.getToken());

            String chatId = String.valueOf(group.getChatId());
            String userId = String.valueOf(botToken.getTelegramId());

            ResponseFromTelegram<ChatMember> response =
                customerFeign.getChatMember(uri, chatId, userId);
            if(response.getOk()){
                log.info("Bot is already exists in other group, Bot token: {} | Group id: {} | Response: {} ",
                    botToken.getToken(), group.getChatId(), response);
                return false;
            }
        }

        return true;
    }

    private void saveToTelegramGroup(Chat chat, CustomerTelegramDTO manager){
        TelegramGroupDTO dto = TelegramGroupDTO.builder()
            .chatId(chat.getId()).name(chat.getTitle()).build();

        dto = telegramGroupService.save(dto);
        Set<TelegramGroupDTO> telegramGroups = manager.getTelegramGroups() == null? new HashSet<>(): manager.getTelegramGroups();
        telegramGroups.add(dto);
//        entityManager.detach(manager);
        manager.setTelegramGroups(telegramGroups);
        manager.setStep(4);

        customerTelegramService.update(manager);
        log.info("Telegram group is saved successfully, Chat id: {} | DTO: {}", chat.getId(), dto);
    }

    private void sayThanksToManager(Long botTelegramId, CustomerTelegramDTO manager){
        String newMessage = "Raxmat☺";
        ReplyKeyboardMarkup menuMarkup = AdminKeyboards.createMenu();

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, menuMarkup);
        Update update = adminFeign.sendMessage(sendMessage);
        log.info("Thanks is send to manager, Bot id: {} | Manager id: {} | Update: {}",
            botTelegramId, manager.getTelegramId(), update);
    }

    private void sendInviteLink(org.telegram.telegrambots.meta.api.objects.User bot, Long groupId){
        String link = "https://t.me/" + bot.getUserName() + "?start=" + groupId;
        String newMessage = "Shu havola orqali botga start bering\uD83D\uDC49 " + link;

        SendMessage sendMessage = TelegramsUtil.sendMessage(groupId, newMessage);
        BotToken botToken = botTokenRepository.findByTelegramId(bot.getId()).get();
        URI uri = createCustomerURI(botToken.getToken());
        Update update = customerFeign.sendMessage(uri, sendMessage);
        log.info("Link is send successfully, Bot id: {} | Groupd id: {} | Uri: {}",
            bot.getId(), groupId, uri);
    }
}
