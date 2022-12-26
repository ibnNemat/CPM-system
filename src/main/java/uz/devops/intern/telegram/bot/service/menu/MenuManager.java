package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.*;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class MenuManager extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.MESSAGE;
    private final String STATE = "MANAGER_MENU";
    private final Integer STEP = 7;

    @Autowired
    private List<ManagerMenuAbs> impls;
    private HashMap<String, ManagerMenuStrategy> menu;

    private final CustomerTelegramService customerTelegramService;
    private final TelegramGroupService telegramGroupService;
    private final UserService userService;

    public MenuManager(CustomerTelegramService customerTelegramService, TelegramGroupService telegramGroupService, UserService userService) {
        this.customerTelegramService = customerTelegramService;
        this.telegramGroupService = telegramGroupService;
        this.userService = userService;
    }

    @PostConstruct
    public void inject(){
        menu = new HashMap<>();
        for(ManagerMenuStrategy m: impls){
            List<String> texts = m.getSupportedTexts();
            for(String text: texts){
                menu.put(text, m);
            }
        }
        log.info("Inner classes are injected, Map size: {} | List: {} | Map: {}",
            menu.size(), impls, menu);
    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
//        if(!update.hasMessage() || !update.getMessage().hasText()){
//            Long userId = update.hasCallbackQuery()? update.getCallbackQuery().getFrom().getId() : null;
//            wrongValue(userId, bundle.getString("bot.admin.send.only.message.or.contact"));
//            log.warn("User didn't send text! User id: {} | Update: {}", userId, update);
//            return false;
//        }

        Long userId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText();

        ManagerMenuStrategy obj = menu.getOrDefault(messageText, menu.get("UNKNOWN"));
        return obj.todo(update, manager);
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public String messageOrCallback() {
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.only.message");
    }

}
