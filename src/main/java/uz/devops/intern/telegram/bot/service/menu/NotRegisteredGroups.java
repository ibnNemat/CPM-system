package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.service.state.BotFSM;

@Service
public class NotRegisteredGroups extends BotStrategyAbs {

    private final String STATE = "TELEGRAM_GROUP_TO_GROUP";
    private final Integer STEP = 6;

    @Autowired
    private BotFSM botFSM;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        log.info("BotFSM is working! Manager: {}", manager);
        return botFSM.execute(update, manager);
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }
}
