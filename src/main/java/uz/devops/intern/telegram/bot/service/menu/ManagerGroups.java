package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.service.dto.TelegramGroupDTO;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerGroups extends ManagerMenuAbs{

    private final String SUPPORTED_TEXT = "\uD83D\uDC65 Guruh qo'shish";

    private final GroupsService groupsService;

    public ManagerGroups(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService, GroupsService groupsService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
        this.groupsService = groupsService;
    }

    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
//            CustomerTelegramDTO manager = customerTelegramService.findByTelegramId(manager.getTelegramId());
        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if (!responseDTO.getSuccess() && responseDTO.getResponseData() == null) {
            log.warn("Manager is not found from jhi_user! Manager: {} | Response: {}", manager, responseDTO);
            return false;
        }

        setUserToContextHolder(responseDTO.getResponseData());

        ResponseDTO<List<TelegramGroupDTO>> response = telegramGroupService.getTelegramGroupsByCustomer(manager.getId());
        if(!response.getSuccess()){
            wrongValue(manager.getTelegramId(), response.getMessage());
            log.warn("{}, Response: {}", response.getMessage(), response);
            return false;
        }

        if (response.getResponseData().isEmpty()) {
            wrongValue(manager.getTelegramId(), "Telegram guruhlar mavjud emas!");
            log.warn("Has no telegram group, Manager id: {} ", manager.getTelegramId());
            return false;
        }
        List<TelegramGroupDTO> telegramGroups = response.getResponseData();

        log.info("Manager telegram groups, Telegram groups count: {} | Telegram groups: {}", telegramGroups.size(), telegramGroups);

        for (TelegramGroupDTO dto : telegramGroups) {
            String newMessage = createGroupText(dto);
            InlineKeyboardMarkup markup = createGroupButtons(dto.getChatId());
            SendMessage sendMessage =
                TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage, markup);
            adminFeign.sendMessage(sendMessage);
        }
        manager.setStep(6);
        return true;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }

    private String createGroupText(TelegramGroupDTO groupDTO){
        ResponseDTO<List<CustomerTelegramDTO>> response =
            customerTelegramService.getCustomerTgByChatId(groupDTO.getId());

        StringBuilder text = new StringBuilder(String.format(
            "Guruh nomi: %s\nFoydalanuvchilar soni: %d\n=========================\n",
            groupDTO.getName(), response.getResponseData().size()
        ));

        List<CustomerTelegramDTO> customerTelegrams = response.getResponseData();
        int index = 1;
        for(CustomerTelegramDTO customer: customerTelegrams){
            text.append(String.format(
                "%d. %s\n", index++, customer.getFirstname()
            ));
        }
        return text.toString();
    }

    private InlineKeyboardMarkup createGroupButtons(Long chatId){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton("Shu guruhni qo'shish");
        button.setCallbackData(String.valueOf(chatId));

        buttons.add(
            List.of(button)
        );

        return new InlineKeyboardMarkup(buttons);
    }

}
