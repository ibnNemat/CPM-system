package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.devops.intern.domain.ResponseFromTelegram;
import uz.devops.intern.telegram.bot.dto.BotCommandsMenuDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.WebhookResponseDTO;


@FeignClient(value = "admin-feign",
    url = "https://api.telegram.org/bot5926613188:AAF3AKO0Yfwc5dk-oFiMvAPDvMGztkGCTkc")
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

    @GetMapping("/leaveChat")
    WebhookResponseDTO leaveChat(@RequestParam("chat_id") String chat_id);

    @PostMapping("/answerCallbackQuery")
    ResponseFromTelegram<Boolean> answerCallbackQuery(@RequestBody AnswerCallbackQuery answerCallbackQuery);
}
