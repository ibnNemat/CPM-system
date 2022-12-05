package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@FeignClient(value = "admin-feign",
    url = "https://api.telegram.org/bot5225793240:AAEDojpbQM780zRMWIvmJXCIGeEBXWoY6RM")
public interface AdminFeign {
    @PostMapping("/sendMessage")
    Update sendMessage(@RequestBody SendMessage sendMessage);
}
