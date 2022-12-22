package uz.devops.intern.telegram.bot.customer.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.PaymentHistory;
import uz.devops.intern.feign.CustomerFeign;
import uz.devops.intern.redis.CallbackDataRedis;
import uz.devops.intern.redis.CallbackRedisRepository;
import uz.devops.intern.redis.CustomerPaymentRedis;
import uz.devops.intern.redis.CustomerPaymentRedisRepository;
import uz.devops.intern.repository.CustomerTelegramRepository;
import uz.devops.intern.service.PaymentHistoryService;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.dto.PaymentHistoryDTO;
import uz.devops.intern.service.mapper.PaymentHistoryMapper;
import uz.devops.intern.telegram.bot.customer.service.CustomerUpdateWithCallbackQueryService;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
import static uz.devops.intern.service.utils.ResourceBundleUtils.getResourceBundleUsingCustomerTelegram;
import static uz.devops.intern.telegram.bot.utils.TelegramCustomerUtils.*;
import static uz.devops.intern.telegram.bot.utils.TelegramsUtil.sendMessage;

@Service
@RequiredArgsConstructor
public class CustomerUpdateWithCallbackQueryServiceImpl implements CustomerUpdateWithCallbackQueryService {
    private static PaymentDTO leftPaymentDTO;
    private static PaymentDTO rightPaymentDTO;
    private static String groupReplyButton;
    private static String paymentHistoryReplyButton;
    private static String paymentReplyButton;
    private static String payReplyButton;
    private static String myProfileReplyButton;
    private static String backHomeMenuButton;
    private static final String DATA_BACK_TO_HOME = "back to menu";
    private static ResourceBundle resourceBundle;
    private final Logger log = LoggerFactory.getLogger(CustomerUpdateWithCallbackQueryServiceImpl.class);
    private final CustomerTelegramRepository customerTelegramRepository;
    private final CustomerPaymentRedisRepository customerPaymentRedisRepository;
    private final CustomerFeign customerFeign;
    private final PaymentHistoryService paymentHistoryService;
    private final CallbackRedisRepository callbackRedisRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;

    @Override
    public SendMessage commandWithCallbackQuery(CallbackQuery callbackQuery, URI telegramURI) {
        log.info("command with callbackQuery after pressed the inline button. CallbackQuery: {} | URI: {}", callbackQuery, telegramURI);
        User telegramUser = callbackQuery.getFrom();
        Optional<CustomerTelegram> optionalCustomerTelegram = customerTelegramRepository.findByTelegramId(telegramUser.getId());
        if (optionalCustomerTelegram.isEmpty())
            return sendCustomerDataNotFoundMessageWithParamTelegramUser(telegramUser);

        CustomerTelegram customerTelegram = optionalCustomerTelegram.get();
        customerTelegram.setIsActive(true);
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);

        String inlineButtonData = callbackQuery.getData().split(" ")[0];
        switch (inlineButtonData) {
            case "left":
            case "right":
                return whenPressingLeftOrRightToGetCustomerPayment(telegramUser, callbackQuery, customerTelegram, telegramURI);
            case "back":
                customerFeign.editMessageReplyMarkup(telegramURI, new EditMessageDTO(String.valueOf(telegramUser.getId()), callbackQuery.getMessage().getMessageId(), callbackQuery.getInlineMessageId(), new InlineKeyboardMarkup()));
                return sendCustomerMenu(telegramUser, customerTelegram);
            case "show":
                customerFeign.editMessageText(telegramURI, showCurrentCustomerPayment(telegramUser, customerTelegram, callbackQuery));
                return new SendMessage();
            case "payment":
                customerFeign.editMessageText(telegramURI, sendRequestPaymentSum(customerTelegram, telegramUser, callbackQuery));
                return new SendMessage();
            case "change":
                customerFeign.editMessageText(telegramURI, changeCustomerProfile(telegramUser, customerTelegram, callbackQuery));
                return new SendMessage();
            default:
                return sendMessage(telegramUser.getId(), resourceBundle.getString(BOT_UNKNOWN_COMMAND));
        }
    }

    @Override
    // Customer can be entered to this menu after registration
    public SendMessage sendCustomerMenu(User userTelegram, CustomerTelegram customerTelegram) {
        customerTelegram.setStep(2);
        customerTelegramRepository.save(customerTelegram);

        return sendMessage(userTelegram.getId(), "\uD83C\uDFE0 Menu", customerMenuReplyKeyboardButtons(customerTelegram));
    }

    @Override
    public EditMessageTextDTO changeCustomerProfile(User telegramUser, CustomerTelegram customerTelegram, CallbackQuery callbackQuery) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        customerTelegram.setStep(2);
        customerTelegramRepository.save(customerTelegram);


        String data = callbackQuery.getData().substring(7);
        List<String> dataList = List.of("email", "phone number", "the balance", "name");
        int step = 4;
        customerTelegram.setStep(step + dataList.indexOf(data));
        customerTelegramRepository.save(customerTelegram);

        return switch (data) {
            case "email" -> createMessageTextDTOWithEmptyInlineButton(resourceBundle.getString(BOT_REQUEST_CHANGE_EMAIL), callbackQuery, telegramUser);
            case "phone number" -> createMessageTextDTOWithEmptyInlineButton(resourceBundle.getString(BOT_REQUEST_CHANGE_PHONE_NUMBER), callbackQuery, telegramUser);
            case "the balance" -> createMessageTextDTOWithEmptyInlineButton(resourceBundle.getString(BOT_REQUEST_CHANGE_BALANCE), callbackQuery, telegramUser);
            default -> createMessageTextDTOWithEmptyInlineButton(resourceBundle.getString(BOT_REQUEST_CHANGE_NAME), callbackQuery, telegramUser);
        };
    }

    @Override
    public EditMessageTextDTO showCurrentCustomerPayment(User telegramUser, CustomerTelegram customerTelegram, CallbackQuery callbackQuery) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        String[] data = callbackQuery.getData().split(" ");
        Long idPaymentHistory = Long.parseLong(data[3]);

        Optional<PaymentHistoryDTO> optionalPaymentHistoryDTO = paymentHistoryService.findOne(idPaymentHistory);

        PaymentHistory paymentHistory = optionalPaymentHistoryDTO
            .map(paymentHistoryMapper::toEntity).get();

        StringBuilder builder = new StringBuilder();
        buildPaymentHistoryMessage(paymentHistory, builder, resourceBundle);

        EditMessageTextDTO editMessageTextDTO = createMessageTextDTO(builder.toString(), callbackQuery, telegramUser);
        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
        editMessageTextDTO.setReplyMarkup(backToMenuInlineButton(customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME));
        return editMessageTextDTO;
    }

    @Override
    public EditMessageTextDTO createMessageTextDTO(String text, CallbackQuery callbackQuery, User telegramUser) {
        EditMessageTextDTO editMessageTextDTO = new EditMessageTextDTO();
        editMessageTextDTO.setChatId(String.valueOf(telegramUser.getId()));
        editMessageTextDTO.setText(text);
        editMessageTextDTO.setParseMode("HTML");
        editMessageTextDTO.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageTextDTO.setInlineMessageId(callbackQuery.getInlineMessageId());
        return editMessageTextDTO;
    }

    @Override
    public EditMessageTextDTO createMessageTextDTOWithEmptyInlineButton(String text, CallbackQuery callbackQuery, User telegramUser) {
        EditMessageTextDTO editMessageTextDTO = createMessageTextDTO(text, callbackQuery, telegramUser);
        editMessageTextDTO.setReplyMarkup(new InlineKeyboardMarkup());
        return editMessageTextDTO;
    }

    @Override
    public EditMessageTextDTO sendRequestPaymentSum(CustomerTelegram customerTelegram, User telegramUser, CallbackQuery callbackQuery) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        customerTelegram.setStep(3);
        customerTelegramRepository.save(customerTelegram);

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);

        CallbackDataRedis callbackDataRedis = new CallbackDataRedis(telegramUser.getId(), callbackQuery.getData());
        callbackRedisRepository.save(callbackDataRedis);

        String stringMessage = resourceBundle.getString(BOT_REQUEST_MONEY_TO_PAYMENT);

        EditMessageTextDTO editMessageTextDTO = createMessageTextDTO(stringMessage, callbackQuery, telegramUser);
        editMessageTextDTO.setReplyMarkup(new InlineKeyboardMarkup());
        return editMessageTextDTO;
    }

    private void setTextToButtons(ResourceBundle resourceBundle){
        paymentHistoryReplyButton = resourceBundle.getString(BOT_PAYMENT_HISTORY_BUTTON);
        paymentReplyButton = resourceBundle.getString(BOT_PAYMENT_BUTTON);
        payReplyButton = resourceBundle.getString(BOT_DEBTS_BUTTON);
        myProfileReplyButton =  resourceBundle.getString(BOT_MY_PROFILE_BUTTON);
        groupReplyButton = resourceBundle.getString(BOT_GROUP_BUTTON);
        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
    }

    @Override
    public SendMessage whenPressingLeftOrRightToGetCustomerPayment(User telegramUser, CallbackQuery callbackQuery, CustomerTelegram customerTelegram, URI telegramURI) {
        String callBackData = callbackQuery.getData();
        Optional<CustomerPaymentRedis> optionalPaymentDTO = customerPaymentRedisRepository.findById(customerTelegram.getId());
        List<PaymentDTO> customerPaymentList = optionalPaymentDTO.get().getPaymentDTOList();

        Long idCustomerTelegram = Long.parseLong(callBackData.split(" ")[1]);
        PaymentDTO customerPaymentToSendInfo = new PaymentDTO();

        for (PaymentDTO paymentDTO : customerPaymentList) {
            if(paymentDTO.getId().equals(idCustomerTelegram)) {
                customerPaymentToSendInfo = paymentDTO;
                break;
            }
        }
        int sizeCustomerPayment = customerPaymentList.size();

        int indexOfCustomerPaymentList = customerPaymentList.indexOf(customerPaymentToSendInfo);
        setIndexOfCustomerPaymentListGetLeftAndRightPayments(customerPaymentList, sizeCustomerPayment, indexOfCustomerPaymentList);
        StringBuilder builder = new StringBuilder();
        buildCustomerPayments(customerPaymentToSendInfo, builder, customerTelegram.getLanguageCode());

        backHomeMenuButton = resourceBundle.getString(BOT_BACK_HOME_BUTTON);
        InlineKeyboardMarkup inlineKeyboardMarkup = paymentOfCustomerInlineMarkupWithLeftAndRightButton(
            customerPaymentToSendInfo, leftPaymentDTO.getId(), rightPaymentDTO.getId(), customerTelegram, backHomeMenuButton, DATA_BACK_TO_HOME
        );
        EditMessageTextDTO messageTextDTO = new EditMessageTextDTO();
        messageTextDTO.setChatId(String.valueOf(telegramUser.getId()));
        messageTextDTO.setText(builder.toString());
        messageTextDTO.setMessageId(callbackQuery.getMessage().getMessageId());
        messageTextDTO.setInlineMessageId(callbackQuery.getInlineMessageId());
        messageTextDTO.setParseMode("HTML");
        messageTextDTO.setReplyMarkup(inlineKeyboardMarkup);

        customerFeign.editMessageText(telegramURI, messageTextDTO);
        return new SendMessage();
    }

    private void setIndexOfCustomerPaymentListGetLeftAndRightPayments(List<PaymentDTO> customerPaymentList, int sizeCustomerPayment, int indexOfCustomerPaymentList){
        if (sizeCustomerPayment == 2 && indexOfCustomerPaymentList == 0){
            leftPaymentDTO = customerPaymentList.get(1);
            rightPaymentDTO = customerPaymentList.get(1);
        }else if(sizeCustomerPayment == 2 && indexOfCustomerPaymentList == 1){
            leftPaymentDTO = customerPaymentList.get(0);
            rightPaymentDTO = customerPaymentList.get(0);
        } else if (indexOfCustomerPaymentList == 0) {
            leftPaymentDTO = customerPaymentList.get(sizeCustomerPayment - 1);
            rightPaymentDTO = customerPaymentList.get(1);
        } else if (indexOfCustomerPaymentList == sizeCustomerPayment-1) {
            leftPaymentDTO = customerPaymentList.get(sizeCustomerPayment - 2);
            rightPaymentDTO = customerPaymentList.get(0);
        }else {
            leftPaymentDTO = customerPaymentList.get(indexOfCustomerPaymentList - 1);
            rightPaymentDTO = customerPaymentList.get(indexOfCustomerPaymentList + 1);
        }
    }


    private ReplyKeyboardMarkup customerMenuReplyKeyboardButtons(CustomerTelegram customerTelegram) {
        resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        setTextToButtons(resourceBundle);

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(groupReplyButton));
        row1.add(new KeyboardButton(paymentHistoryReplyButton));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(paymentReplyButton));
        row2.add(new KeyboardButton(payReplyButton));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton(myProfileReplyButton));

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setKeyboard(List.of(row1, row2, row3));
        return markup;
    }
}
