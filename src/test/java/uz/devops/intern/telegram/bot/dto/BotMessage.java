package uz.devops.intern.telegram.bot.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BotMessage extends Message {

    private Long id;
    private String text;
    private User from;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
    private ReplyKeyboardMarkup replyKeyboardMarkup;

    public void setId(Long id) {
        setId(id);
    }

    @Override
    public void setText(String text) {
        setText(text);
    }

    @Override
    public void setFrom(User from) {
        setFrom(from);
    }

    public void setInlineKeyboardMarkup(InlineKeyboardMarkup inlineKeyboardMarkup) {
        setInlineKeyboardMarkup(inlineKeyboardMarkup);
    }

    public void setReplyKeyboardMarkup(ReplyKeyboardMarkup replyKeyboardMarkup) {
        setReplyKeyboardMarkup(replyKeyboardMarkup);
    }
}
