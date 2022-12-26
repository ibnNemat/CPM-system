package uz.devops.intern.telegram.bot.service.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.service.state.ServiceFSM;
import uz.devops.intern.telegram.bot.service.state.ServiceNameState;

import java.util.ResourceBundle;

@Service
public class RegisterNewService extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.BOTH_OF_THEM;
    private final String STATE = "MANAGER_ADD_NEW_SERVICE";
    private final Integer STEP = 8;

    @Autowired
    private ServiceFSM serviceFSM;

    @Autowired
    private ServiceNameState serviceNameState;

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        log.info("Manager's registering new service, Manager: {}", manager);
        return serviceFSM.execute(update, manager);
    }

    public void rollbackStateToStart(){
        serviceFSM.changeState(serviceNameState);
//        serviceFSM.changeState(new ServiceNameState(serviceFSM));
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
