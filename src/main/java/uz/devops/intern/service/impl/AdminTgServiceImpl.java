package uz.devops.intern.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.feign.TelegramClient;
import uz.devops.intern.service.AdminTgService;
import uz.devops.intern.service.CustomersService;

import java.util.List;

@Service
public class AdminTgServiceImpl implements AdminTgService {

    private final Logger log = LoggerFactory.getLogger(AdminTgServiceImpl.class);

    @Autowired
    private CustomersService customersService;
    @Autowired
    private TelegramClient feign;

    @Override
    public void pressedStartCommand(Message message) {
        Long userId = message.getFrom().getId();
        String messageText = message.getText();

        String newMessage = "Iltimos tilni tanlang\uD83D\uDC47";
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("\uD83C\uDDFA\uD83C\uDDFF O`zbekcha"));
        row.add(new KeyboardButton("\uD83C\uDDF7\uD83C\uDDFA Русский"));

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(List.of(row));

    }

    @Override
    public void verifyAdminByPhoneNumber(Message message) {

    }

    @Override
    public void getAdminBotToken(Message message) {

    }
}
