package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.telegram.bot.dto.BotCommandsMenuDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;

import java.net.URI;

@FeignClient(value = "customer-feign",
        url = "https://api.telegram.org/bot")
public interface CustomerFeign {
    @PostMapping("/sendMessage")
    Update sendMessage(URI uri, @RequestBody SendMessage sendMessage);

    @PostMapping("/editMessageReplyMarkup")
    Message editMessageReplyMarkup(URI uri, @RequestBody EditMessageDTO editedMessageDTO);

    @PostMapping("/editMessageText")
    Message editMessageText(URI uri, @RequestBody EditMessageTextDTO editMessageTextDTO);
    @PostMapping("/setMyCommands")
    ResponseFromTelegram<Boolean> setMyCommands(URI uri,@RequestBody BotCommandsMenuDTO botCommands);
    @DeleteMapping("/deleteMessage")
    Boolean deleteMessage(URI uri, @RequestParam(name = "chat_id") String chat_id, @RequestParam(name = "message_id") Integer message_id);
}
