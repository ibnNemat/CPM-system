package uz.devops.intern.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@FeignClient(value = "customer-feign",
        url = "https://api.telegram.org/bot5344114476:AAHjU99o-rTS_0OU-tDUk6zdUXmWj7pyc10")
// 5891322238:AAHojS64GBG3tKSz2zWtxQ41aydkyub5Dag
public interface CustomerFeign {
    @PostMapping("/sendMessage")
    Update sendMessage(@RequestBody SendMessage sendMessage);
}
