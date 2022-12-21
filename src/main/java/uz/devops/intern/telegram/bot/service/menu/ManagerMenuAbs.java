package uz.devops.intern.telegram.bot.service.menu;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.domain.User;
import uz.devops.intern.feign.AdminFeign;
import uz.devops.intern.service.CustomerTelegramService;
import uz.devops.intern.service.TelegramGroupService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public abstract class ManagerMenuAbs implements ManagerMenuStrategy {

    public final Logger log = LoggerFactory.getLogger(ManagerMenuAbs.class);

//    public final String STATE = "MANAGER_MENU";
//    public final Integer STEP = 4;

    public final AdminFeign adminFeign;

    public final CustomerTelegramService customerTelegramService;
    public final TelegramGroupService telegramGroupService;
    public final UserService userService;

    public void setUserToContextHolder(User user){
        Set<GrantedAuthority> authorities =
            user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName())).collect(Collectors.toSet());

        org.springframework.security.core.userdetails.User principal =
            new org.springframework.security.core.userdetails.User(user.getLogin(),
                user.getPassword() == null? "": user.getPassword(), authorities);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void wrongValue(Long chatId, String message){
        SendMessage sendMessage = TelegramsUtil.sendMessage(chatId, message);
        Update update = adminFeign.sendMessage(sendMessage);
        log.warn("User send invalid value, Chat id: {} | Message: {} | Update: {}",
            chatId, message, update);
    }

//    public void messageHasNotText(Long chatId, Update update){
//        ResourceBundleUtils.getResourceBundleByUserLanguageCode("ru");
//        wrongValue(chatId, "Iltimos xabar yuboring\uD83D\uDE4F");
//        log.warn("User hasn't send text, Chat id: {} | Update: {}", chatId, update);
//    }
//
//    public void messageHasNotText(Long chatId, Update update, Boolean contact){
//        wrongValue(chatId, "Iltimos xabar yoki kontakt yuboring\uD83D\uDE4F");
//        log.warn("User hasn't send text, Chat id: {} | Update: {}", chatId, update);
//    }

}
