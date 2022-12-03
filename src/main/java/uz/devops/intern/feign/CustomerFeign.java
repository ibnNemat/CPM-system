package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@FeignClient(value = "customer-feign",
        url = "https://api.telegram.org/bot5156167848:AAHnrLe0T8FnbF8vK-3LiR8bAjUrGxnTzR4")
public interface CustomerFeign {
    @PostMapping("/sendMessage")
    Update sendMessage(@RequestBody SendMessage sendMessage);
}
