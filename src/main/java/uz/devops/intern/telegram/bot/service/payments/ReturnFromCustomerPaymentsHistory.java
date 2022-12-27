package uz.devops.intern.telegram.bot.service.payments;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.User;
import uz.devops.intern.service.GroupsService;
import uz.devops.intern.service.PaymentService;
import uz.devops.intern.service.UserService;
import uz.devops.intern.service.dto.*;
import uz.devops.intern.service.utils.ResourceBundleUtils;
import uz.devops.intern.telegram.bot.AdminKeyboards;
import uz.devops.intern.telegram.bot.dto.EditMessageDTO;
import uz.devops.intern.telegram.bot.dto.EditMessageTextDTO;
import uz.devops.intern.telegram.bot.dto.UpdateType;
import uz.devops.intern.telegram.bot.service.BotStrategyAbs;
import uz.devops.intern.telegram.bot.utils.KeyboardUtil;
import uz.devops.intern.telegram.bot.utils.TelegramsUtil;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReturnFromCustomerPaymentsHistory extends BotStrategyAbs {
    private final UpdateType SUPPORTED_TYPE = UpdateType.CALLBACK_QUERY;
    private final String STATE = "MANAGER_RETURN_TO_BACK_OR_MENU";
    private final Integer STEP = 15;
    private final Integer NEXT_STEP = 7;

    private final GroupsService groupsService;
    private final PaymentService paymentService;
    private final UserService userService;

//    private final Map<String, Method> methodsMap = new HashMap<>();
//
//    @PostConstruct
//    public void fillMap(){
//        final String KEY = "bot.admin.keyboard.for.menu";
//
//        List<String> languages = KeyboardUtil.availableLanguages();
//        Map<String, String> languagesMap = KeyboardUtil.getLanguages();
//        for(String language: languages){
//            String languageCode = languagesMap.get(language);
//            ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(languageCode);
//
//        }
//    }

    @Override
    public boolean execute(Update update, CustomerTelegramDTO manager) {
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleByUserLanguageCode(manager.getLanguageCode());
//        return getReplyButtonText(update, manager);
        boolean result = didUserPressedLeftOrRight(update.getCallbackQuery(), manager);
        if(result){
           return false;
        }

        String[] data = update.getCallbackQuery().getData().split(":");
        String paymentIdAsString = data[0];
        String userLogin = data[2];
        Long paymentId = null;
        try{
            paymentId = Long.parseLong(paymentIdAsString);
        }catch (NumberFormatException e){
            log.warn("Error while parsing String to Long! Exception: {} ", e.getMessage());
            return false;
        }

        Optional<PaymentDTO> paymentOptional = paymentService.findOne(paymentId);
        if(paymentOptional.isEmpty()){
            log.warn("Payment is not found! Payment id: {} ", paymentId);
            return false;
        }

        PaymentDTO payment = paymentOptional.get();
        ResponseDTO<User> userResponse = userService.getUserByLogin(userLogin);
        String messageText = createText(payment, userResponse.getResponseData(), bundle);
        EditMessageTextDTO editMessageTextDTO =
            createEditMessage(update.getCallbackQuery(), update.getCallbackQuery().getMessage().getReplyMarkup(), messageText);
        try{
            adminFeign.editMessageText(editMessageTextDTO);
            return true;
        }catch (FeignException e){
            AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .text(bundle.getString("bot.admin.error.payment.you.have.current")).callbackQueryId(update.getCallbackQuery().getId()).build();
            adminFeign.answerCallbackQuery(answerCallbackQuery);
            return false;
        }
    }

    public boolean getReplyButtonText(Update update, CustomerTelegramDTO manager){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(manager.getLanguageCode());
        String messageText = update.getMessage().getText();

        if(!messageText.equals(bundle.getString("bot.admin.keyboard.for.menu"))){
            wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.error.message"));
            log.warn("Manager send wrong message! Manager id: {} | Message text: {} ", manager.getTelegramId(), messageText);
            return false;
        }

        throwManagerToMenu(manager, bundle);
        return true;
    }

    @Override
    public String getState() {
        return STATE;
    }

    @Override
    public Integer getStep() {
        return STEP;
    }

    @Override
    public String messageOrCallback() {
        return SUPPORTED_TYPE.name();
    }

    @Override
    public String getErrorMessage(ResourceBundle bundle) {
        return bundle.getString("bot.admin.error.choose.up");
    }

    private InlineKeyboardMarkup createCustomersInlineButtonsList(GroupsDTO group, ResourceBundle bundle){

        List<List<InlineKeyboardButton>> keyboardButtons = new ArrayList<>();
        for(CustomersDTO customer: group.getCustomers()){
            keyboardButtons.add(
                List.of(
                    InlineKeyboardButton.builder().text(customer.getUsername()).callbackData(String.valueOf(customer.getId())).build()
                )
            );
        }

        keyboardButtons.add(
            List.of(
                InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.back")).callbackData("BACK").build()
            )
        );

        return InlineKeyboardMarkup.builder().keyboard(keyboardButtons).build();
    }

    private void removeInlineButtons(CallbackQuery callback, ResourceBundle bundle){
        EditMessageDTO editMessageDTO = new EditMessageDTO();
        editMessageDTO.setChatId(String.valueOf(callback.getFrom().getId()));
        editMessageDTO.setMessageId(callback.getMessage().getMessageId());
        editMessageDTO.setInlineMessageId(callback.getInlineMessageId());
        editMessageDTO.setReplyMarkup(new InlineKeyboardMarkup());

        try {
            adminFeign.editMessageReplyMarkup(editMessageDTO);
            log.info("Inline buttons are removed, Manager id: {}", callback.getFrom().getId());
        }catch (FeignException e){
            log.error("Error while remove inline buttons! Manager id: {} | Exception: {}", callback.getFrom().getId(), e.getMessage());
            wrongValue(callback.getFrom().getId(), bundle.getString("bot.admin.error.please.connect.to.developer"));
        }

    }

    private boolean didUserPressedLeftOrRight(CallbackQuery callback, CustomerTelegramDTO manager){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(manager.getLanguageCode());

        String callbackData = callback.getData();
        if(callbackData.contains("RIGHT")){
            EditMessageTextDTO editMessageTextDTO = methodToLeftOrRight(callback, manager, "RIGHT");
            if(Objects.isNull(editMessageTextDTO)){
                answerToCallback(bundle, callback.getId());
                return true;
            }
            try{
                adminFeign.editMessageText(editMessageTextDTO);
                return true;
            }catch (FeignException e){
                AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                    .text(bundle.getString("bot.admin.error.payments.not")).callbackQueryId(callback.getId()).build();
                adminFeign.answerCallbackQuery(answerCallbackQuery);
                return true;
            }
//            adminFeign.editMessageText(editMessageTextDTO);
//            log.info("Message is edited, Callback data: {} ", callbackData);
//            return true;

        }else if(callbackData.contains("LEFT")){
            EditMessageTextDTO editMessageTextDTO = methodToLeftOrRight(callback, manager, "LEFT");
            if(Objects.isNull(editMessageTextDTO)){
                answerToCallback(bundle, callback.getId());
                return true;
            }
            try{
                adminFeign.editMessageText(editMessageTextDTO);
                return true;
            }catch (FeignException e){
                AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                    .text(bundle.getString("bot.admin.error.payments.not")).callbackQueryId(callback.getId()).build();
                adminFeign.answerCallbackQuery(answerCallbackQuery);
                return true;
            }
//            adminFeign.editMessageText(editMessageTextDTO);
//            log.info("Message is edited, Callback data: {} ", callbackData);
//            return true;

        }else if(callbackData.contains("HOME")){
            boolean result = removeInlineButtons(callback);
            if(!result){
                wrongValue(manager.getTelegramId(), bundle.getString("bot.admin.main.menu"));
                return true;
            }
            throwManagerToMenu(manager, bundle);
            return true;
        }

        return false;
    }

    private EditMessageTextDTO methodToLeftOrRight(CallbackQuery callback, CustomerTelegramDTO manager, String rightOrLeft){
        ResourceBundle bundle = ResourceBundleUtils.getResourceBundleUsingLanguageCode(manager.getLanguageCode());
        String[] data = callback.getData().split(":");
        String paymentIdAsString = data[0];
        String userLogin = data[2];
        Integer page = parseStringToLong(paymentIdAsString);
        if(Objects.isNull(page)) return null;
        ResponseDTO<User> userResponse = userService.getUserByLogin(userLogin);
        if(!userResponse.getSuccess() ||  Objects.isNull(userResponse.getResponseData()))return null;
//        page = rightOrLeft.equals("RIGHT")? page + 1: page - 1;
        List<PaymentDTO> customerPayments = getUserPayments(userLogin, page);
        if(Objects.isNull(customerPayments))return null;
        InlineKeyboardMarkup markup = createPaymentsHistoryPagination(bundle, customerPayments, userLogin, page);
        String messageText = createText(customerPayments.get(0), userResponse.getResponseData(),bundle);

        return createEditMessage(callback, markup, messageText);
    }

    private List<PaymentDTO> getUserPayments(String userLogin, Integer page){
        if(page < 0)page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        ResponseDTO<List<PaymentDTO>> paymentsResponse = paymentService.getByUserLogin(userLogin, pageable);
        if(!paymentsResponse.getSuccess() || paymentsResponse.getResponseData().isEmpty()){
            return null;
        }

        return paymentsResponse.getResponseData();
    }


    private Integer parseStringToLong(String page) {
        try {
            return Integer.parseInt(page);
        } catch (NumberFormatException e) {
            log.error("Error while parsing string to integer! Source: {} | Exception: {}",
                page, e.getMessage());
            return null;
        }
    }

    private InlineKeyboardMarkup createPaymentsHistoryPagination(ResourceBundle bundle, List<PaymentDTO> payments, String customerLogin, Integer page){

        List<List<InlineKeyboardButton>> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int i = 1;

        for(PaymentDTO payment: payments){
            row.add(
                InlineKeyboardButton.builder()
                    .text(String.valueOf(i))
                    .callbackData(payment.getId() + ":" + i++ + ":" + customerLogin)
                    .build()
            );

            if(row.size() == 5){
                inlineKeyboardButtons.add(row);
                row = new ArrayList<>();
            }
        }

        if(!row.isEmpty()){
            inlineKeyboardButtons.add(row);
        }

        List<InlineKeyboardButton> footer = List.of(
            InlineKeyboardButton.builder().text("⬅️").callbackData((page - 1 < 0? 0 + "": page - 1) + ":LEFT" + ":" +  customerLogin).build(),
            InlineKeyboardButton.builder().text(bundle.getString("bot.admin.keyboard.for.menu.only.text")).callbackData("HOME").build(),
            InlineKeyboardButton.builder().text("➡️").callbackData(page + 1 + ":RIGHT" + ":" + customerLogin).build()
        );

        inlineKeyboardButtons.add(footer);

        return InlineKeyboardMarkup.builder().keyboard(inlineKeyboardButtons).build();
    }

    private String createText(PaymentDTO payment, User user, ResourceBundle bundle){
        ServicesDTO service = payment.getService();
        return String.format(bundle.getString("bot.admin.send.text.customer.payment.history"),
            user.getFirstName() + " " + user.getLastName(),
            user.getCreatedBy(),
            service.getName(),
            payment.getStartedPeriod(),
            payment.getPaidMoney(),
            service.getPrice(),
            service.getPrice() - payment.getPaidMoney());
    }

    private void answerToCallback(ResourceBundle bundle, String callbackId){
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
            .text(bundle.getString("bot.admin.error.payments.not"))
            .callbackQueryId(callbackId)
            .build();

        adminFeign.answerCallbackQuery(answerCallbackQuery);
    }

}
