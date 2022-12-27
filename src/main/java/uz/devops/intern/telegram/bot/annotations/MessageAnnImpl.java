package uz.devops.intern.telegram.bot.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.telegram.telegrambots.meta.api.objects.Update;

@Aspect
public class MessageAnnImpl {

    @Pointcut(value = "@annotation(uz.devops.intern.telegram.bot.annotations.OnlyMessage) && args(onlyMessage, update,..)", argNames = "onlyMessage,update")
    public void callIt(OnlyMessage onlyMessage, Update update){
    }

    @Around(value = "callIt(onlyMessage, update)", argNames = "pjp,onlyMessage,update")
    public Object implementationWithAround(ProceedingJoinPoint pjp, OnlyMessage onlyMessage, Update update) throws Throwable {
        if(onlyMessage.onlyMessage()){
            return update.hasMessage()? pjp.proceed(): null;
        }else if(onlyMessage.onlyCallback()){
            return update.hasCallbackQuery()? pjp.proceed(): null;
        }
        return null;
    }

}
