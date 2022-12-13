package uz.devops.intern.telegram.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class EditMessageDTO {
    @JsonProperty("chat_id")
    private String chatId;
    @JsonProperty("message_id")
    private Integer messageId;
    @JsonProperty("inline_message_id")
    private String inlineMessageId;
    @JsonProperty("reply_markup")
    private InlineKeyboardMarkup replyMarkup;

    public EditMessageDTO(){}

    public EditMessageDTO(String chatId, Integer messageId, String inlineMessageId, InlineKeyboardMarkup replyMarkup) {
        this.chatId = chatId;
        this.messageId = messageId;
        this.inlineMessageId = inlineMessageId;
        this.replyMarkup = replyMarkup;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getInlineMessageId() {
        return inlineMessageId;
    }

    public void setInlineMessageId(String inlineMessageId) {
        this.inlineMessageId = inlineMessageId;
    }

    public InlineKeyboardMarkup getReplyMarkup() {
        return replyMarkup;
    }

    public void setReplyMarkup(InlineKeyboardMarkup replyMarkup) {
        this.replyMarkup = replyMarkup;
    }
}
