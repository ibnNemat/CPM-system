package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;

@FeignClient(value = "customer-feign",
        url = "https://api.telegram.org/bot")
public interface CustomerFeign {
    @PostMapping("/sendMessage")
    Update sendMessage(URI uri, @RequestBody SendMessage sendMessage);
}
