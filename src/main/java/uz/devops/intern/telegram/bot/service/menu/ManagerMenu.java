package uz.devops.intern.telegram.bot.service.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.service.dto.CustomerTelegramDTO;
import uz.devops.intern.telegram.bot.service.CommandHalfImpl;

@Service
@RequiredArgsConstructor
public class ManagerMenu extends CommandHalfImpl {

    private final String STATE = "MANAGER_MAIN_MENU";
    private final Integer STEP = 4;


    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        if(!update.hasMessage()){

        }

        return false;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public Integer getStep() {
        return null;
    }
}
