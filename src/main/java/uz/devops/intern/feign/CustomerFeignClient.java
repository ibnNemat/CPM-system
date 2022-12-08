package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import uz.devops.intern.domain.ResponseFromTelegram;

import java.net.URI;

@FeignClient(name = "customer-feign-client", url = "https://api.telegram.org/bot")
public interface CustomerFeignClient {

    @PostMapping("/sendMessage")
    Update sendMessage(URI uri, @RequestBody SendMessage sendMessage);

    @GetMapping("/getMe")
    ResponseFromTelegram<User> getMe(URI uri);

    @GetMapping("/getChatMember")
    ResponseFromTelegram<ChatMember> getChatMember(URI uri, @Param("chat_id") Integer chat_id, @Param("user_id") Integer user_id);
}
