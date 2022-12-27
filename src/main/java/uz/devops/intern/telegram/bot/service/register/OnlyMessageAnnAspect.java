package uz.devops.intern.telegram.bot.service.register;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.telegram.bot.annotations.OnlyMessage;

@Service
public class OnlyMessageAnnAspect {

    @OnlyMessage(onlyCallback = true)
    public boolean run(Update update){
        System.out.println("DGDSH");
        return true;
    }
}
