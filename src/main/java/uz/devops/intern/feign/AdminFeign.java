package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.telegram.bot.dto.BotCommandsMenuDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;

import java.util.List;

@FeignClient(value = "admin-feign",
    url = "https://api.telegram.org/bot5225793240:AAEDojpbQM780zRMWIvmJXCIGeEBXWoY6RM")
//    url = "https://api.telegram.org/bot5543292898:AAGoR3GLOCOL7Lir7sjYyCFYS7BLiUwNbHA")
public interface AdminFeign {
    @PostMapping("/sendMessage")
    Update sendMessage(@RequestBody SendMessage sendMessage);

    @PostMapping("/editMessageReplyMarkup")
    Message editMessageReplyMarkup(@RequestBody EditMessageDTO editMessageDTO);

    @PostMapping("/editMessageText")
    Message editMessageText(@RequestBody EditMessageTextDTO editMessageTextDTO);

    @PostMapping("/setMyCommands")
    ResponseFromTelegram<Boolean> setMyCommands(@RequestBody BotCommandsMenuDTO botCommands);

    @PostMapping("/deleteMyCommands")
    ResponseFromTelegram<Boolean> deleteMyCommands();
}
