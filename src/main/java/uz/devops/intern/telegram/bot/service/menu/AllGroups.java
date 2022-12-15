package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.service.dto.GroupsDTO;
import uz.devops.intern.service.dto.ResponseDTO;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.List;

@Service
public class AllGroups extends ManagerMenuAbs{
    private final String SUPPORTED_TEXT = "\uD83D\uDC40 Guruhlarni ko'rish";

    @Autowired
    private GroupsService groupsService;

    public AllGroups(AdminFeign adminFeign, CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        super(adminFeign, customerTelegramService, telegramGroupService, userService);
    }


    @Override
    public boolean todo(Update update, CustomerTelegramDTO manager) {
        ResponseDTO<User> responseDTO = userService.getUserByPhoneNumber(manager.getPhoneNumber());
        if(!responseDTO.getSuccess()){
            wrongValue(manager.getTelegramId(), responseDTO.getMessage());
            log.warn("{} | Manager id: {} | Response: {}", responseDTO.getMessage(), manager.getTelegramId(), responseDTO);
            return false;
        }

        setUserToContextHolder(responseDTO.getResponseData());

        List<GroupsDTO> groups = groupsService.findOnlyManagerGroups();
        if(groups.isEmpty()){
            wrongValue(manager.getTelegramId(), "Tashkilotlarga biriktirilgan tashkilotlar hozircha mavjud emas!");
            log.warn("Manager has not any groups! Manager: {}", manager);
            return false;
        }

        StringBuilder newMessage = new StringBuilder();
        for(GroupsDTO group: groups){
            newMessage.append(String.format(
                "Guruh nomi: <b>%s\n</b>" +
                    "Tashkilot nomi: <b>%s\n</b>" +
                    "Foydalanuvchilar soni: <b>%d</b>\n\n",
                group.getName(), group.getOrganization().getName(), group.getCustomers().size()
            ));
        }

        SendMessage sendMessage = TelegramsUtil.sendMessage(manager.getTelegramId(), newMessage.toString());
        adminFeign.sendMessage(sendMessage);
        return true;
    }

    @Override
    public String getSupportedText() {
        return SUPPORTED_TEXT;
    }
}
