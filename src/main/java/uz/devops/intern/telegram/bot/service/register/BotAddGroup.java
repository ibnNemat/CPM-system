package uz.devops.intern.telegram.bot.service.register;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.BotToken;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.feign.CustomerFeignClient;
import uz.devops.intern.repository.BotTokenRepository;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.dto.WebhookResponseDTO;
import uz.devops.intern.telegram.bot.keyboards.AdminMenuKeys;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;
import uz.devops.intern.web.rest.utils.WebUtils;

import java.net.URI;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BotAddGroup extends BotStrategyAbs {

    private final String STATE = "MANAGER_BOT_ADD_GROUP";
    private final Integer STEP = 5;
    private final Integer NEXT_STEP = 6;

    private final BotTokenService botTokenService;
    private final UserService userService;
    private final GroupsService groupsService;
    private final TelegramGroupService telegramGroupService;
    private final CustomerTelegramService customerTelegramService;
    private final OrganizationService organizationService;

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        if(!update.hasMessage() || !update.getMessage().hasText()){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.message"));
            log.warn("User didn't send text! User id: {} | Update: {}", manager.getTelegramId(), update);
            return false;
        }

        String messageText = update.getMessage().getText();
        if(!messageText.equals(bundle.getString("bot.admin.those.all"))){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.message"));
            log.warn("User didn't send text! User id: {} | Update: {}", manager.getTelegramId(), update);
            return false;
        }
        // Birinchi "Shularni hammasi" digan buttonni yoqotish kere keyin organizatsiyalar ko'rsatilishi kere
        boolean result = sendMessageToRemoveButton(manager, bundle);
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }
        result = basicFunctionality(manager, bundle);
        if(!result){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
            return false;
        }
        manager.setStep(NEXT_STEP);
        return true;
    }

    public boolean sendMessageToRemoveButton(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.send.all.groups.attached.to.one.organization");
        ReplyKeyboardRemove removeKeyboard = ReplyKeyboardRemove.builder().removeKeyboard(true).build();
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, removeKeyboard);
        try {
            adminFeign.sendMessage(sendMessage);
            return true;
        }catch (FeignException.FeignClientException e){
            log.warn("Thrown error while sending message to user when user choose organization! Manager id: {} | Ex. message: {} | Ex. cause: {}",
                manager.getTelegramId(), e.getMessage(), e.getCause().toString());
            return false;
        }
    }

    public boolean basicFunctionality(CustomerTelegramDTO manager, ResourceBundle bundle){
        String newMessage = bundle.getString("bot.admin.send.choose.one.organization");
        InlineKeyboardMarkup markup = createManagerOrganizationsButton(manager.getPhoneNumber());
        if(markup == null){
            return false;
        }
        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    private InlineKeyboardMarkup createManagerOrganizationsButton(String managerPhoneNumber){
        ResponseDTO<uz.devops.intern.domain.User> responseDTO = userService.getUserByPhoneNumber(managerPhoneNumber);
        if(!responseDTO.getSuccess()){
            log.warn("User(jhi) is not found! Manager phone number: {}", managerPhoneNumber);
            return null;
        }
        WebUtils.setUserToContextHolder(responseDTO.getResponseData());
        List<OrganizationDTO> organizations = organizationService.getOrganizationsByUserLogin();
        if(organizations.isEmpty()){
            log.warn("Organizations not found! Manager phone number: {}", managerPhoneNumber);
            return null;
        }

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for(OrganizationDTO organization: organizations){
            List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder()
                    .text(organization.getName())
                    .callbackData(String.valueOf(organization.getId())
                    ).build());

            rows.add(buttons);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    public void execute(Update update, String botId){

        if(update.getMessage().getNewChatMembers().size() > 0){
            log.info("New bot, Bot id: {}", botId);
            checkIsBotInGroup(update.getMessage().getNewChatMembers().get(0), update.getMessage().getChat(), botId);
        }else if(update.getMessage().getLeftChatMember() != null && update.getMessage().getLeftChatMember().getIsBot()){
            log.info("Bot is kicked! Bot id: {}", botId);
            botIsLeft(update.getMessage().getLeftChatMember());
        }
    }

    public boolean botIsLeft(User bot){
        adminFeign.sendMessage(
            SendMessage.builder()
                .chatId("736527480")
                .text("New bot is add! Bot: " + bot.getId() + " " + bot.getUserName()).build());
        ResponseDTO<BotTokenDTO> responseDTO = botTokenService.findByChatId(bot.getId(), true);
        if(!responseDTO.getSuccess()){
            log.warn("Bot is not found! Bot telegram id: {} | User: {} ", bot.getId(), bot);
            return false;
        }
        URI uri = createCustomerURI(responseDTO.getResponseData().getToken());
//        WebhookResponseDTO response = customerFeign.deleteWebhook(uri, true);
//        log.info("Webhook is delete! Bot: {} | Response: {}", responseDTO.getResponseData(), response);
        return true;
    }

    public boolean checkIsBotInGroup(User user, Chat chat, String botId) {
        boolean isBot = user.getIsBot();
        if(!isBot){
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
        if(customerTgResponse.getResponseData().getStep() != 5){
            ResourceBundle bundle =
                ResourceBundleUtils.getResourceBundleByUserLanguageCode(customerTgResponse.getResponseData().getLanguageCode());
            wrongValue(customerTgResponse.getResponseData().getTelegramId(), bundle.getString("bot.admin.error.user.can.not.add.bot.to.group"));
        }
        CustomerTelegramDTO manager = customerTgResponse.getResponseData();

        GroupsDTO group = saveAsGroup(chat, manager.getPhoneNumber());
        sendInviteLink(user, chat.getId());
        saveToTelegramGroup(chat, manager);
        sayThanksToManager(manager, chat.getTitle(), group.getName(), user.getUserName());
        log.info("Thanks is send to manager, Bot id: {} | Manager id: {}",
            user.getId(), manager.getTelegramId());
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
//        manager.setStep(NEXT_STEP);

        customerTelegramService.update(manager);
        log.info("Telegram group is saved successfully, Chat id: {} | DTO: {}", chat.getId(), dto);
    }

    private void sayThanksToManager(CustomerTelegramDTO manager, String tgGroupName, String groupName, String botName){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
        String newMessage = String.format(bundle.getString("bot.admin.bot.say.thanks"), tgGroupName, groupName, botName);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(
            List.of(new KeyboardRow(
                List.of(
                    new KeyboardButton(bundle.getString("bot.admin.those.all"))
                ))
            )
        );

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
        adminFeign.sendMessage(sendMessage);
    }

    private GroupsDTO saveAsGroup(Chat chat, String phoneNumber){
        ResponseDTO<GroupsDTO> response = groupsService.findByName(chat.getTitle());
        String groupName = response.getSuccess()? chat.getTitle() + chat.getId(): chat.getTitle();

        ResponseDTO<uz.devops.intern.domain.User> responseDTO = userService.getUserByCreatedBy(phoneNumber, true);
        if(!responseDTO.getSuccess()){
            log.warn("User is not found while saving new group! User phone number: {}", phoneNumber);
            return null;
        }
        WebUtils.setUserToContextHolder(responseDTO.getResponseData());
        GroupsDTO group = GroupsDTO.builder()
            .name(groupName).groupOwnerName(responseDTO.getResponseData().getFirstName()).build();
        return groupsService.save(group);
    }

    private void sendInviteLink(User bot, Long groupId){
        ResourceBundle bundleUz = ResourceBundleUtils.getResourceBundleByUserLanguageCode("uz");
        ResourceBundle bundleRu = ResourceBundleUtils.getResourceBundleByUserLanguageCode("ru");
        ResourceBundle bundleEn = ResourceBundleUtils.getResourceBundleByUserLanguageCode("en");

        String href = " <a href=\"https://t.me/" + bot.getUserName() + "?start=" + groupId + "\">";
        String endHref = "</a>\n\n";
        String newMessage =
            bundleUz.getString("bot.admin.bot.send.invite.link") + href +
                bundleUz.getString("bot.admin.send.command.start.to.bot") + endHref +
                bundleRu.getString("bot.admin.bot.send.invite.link") + href +
                bundleRu.getString("bot.admin.send.command.start.to.bot") + endHref +
                bundleEn.getString("bot.admin.bot.send.invite.link") + href +
                bundleEn.getString("bot.admin.send.command.start.to.bot") + endHref;

        SendMessage sendMessage = TelegramsUtil.sendMessage(groupId, newMessage);
        ResponseDTO<BotTokenDTO> response =
            botTokenService.findByChatId(bot.getId(), true);

        URI uri = createCustomerURI(response.getResponseData().getToken());
        try {
            customerFeign.sendMessage(uri, sendMessage);
            log.info("Link is send successfully, Bot id: {} | Groupd id: {} | Uri: {}",
                bot.getId(), groupId, uri);
        }catch (FeignException e){
            log.error("Error while sending! Error: {}", e.getMessage().toString());
            adminFeign.sendMessage(
                SendMessage.builder().chatId("736527480").text(e.getMessage().toString()).build()
            );
        }
    }
}
