package uz.devops.intern.telegram.bot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.devops.intern.domain.CustomerTelegram;
import uz.devops.intern.domain.PaymentHistory;
import uz.devops.intern.service.dto.PaymentDTO;
import uz.devops.intern.service.utils.DateUtils;

import java.util.List;
import java.util.ResourceBundle;

import static uz.devops.intern.constants.ResourceBundleConstants.*;
import static uz.devops.intern.constants.StringFormatConstants.*;
import static uz.devops.intern.service.utils.ResourceBundleUtils.*;

public class TelegramCustomerUtils {
    public static void buildPaymentHistoryMessage(PaymentHistory paymentHistory, StringBuilder buildCustomerPaymentHistories, ResourceBundle resourceBundle) {
        buildCustomerPaymentHistories.append(String.format(STRING_FORMAT_ONE_TEXT_ONE_LONG, resourceBundle.getString(BOT_NUMBER_PAYMENT_HISTORY),paymentHistory.getId()));
        buildCustomerPaymentHistories.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_TYPE_SERVICE), paymentHistory.getServiceName()));
        buildCustomerPaymentHistories.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_ORGANIZATION_NAME), paymentHistory.getOrganizationName()));
        buildCustomerPaymentHistories.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_GROUP_NAME), paymentHistory.getGroupName()));
        buildCustomerPaymentHistories.append(String.format(STRING_FORMAT_ONE_TEXT_ONE_FLOAT, resourceBundle.getString(BOT_PAID_MONEY), paymentHistory.getSum()));
        buildCustomerPaymentHistories.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_DATE_PAID_MONEY), DateUtils.parseToStringFromLocalDate(paymentHistory.getCreatedAt())));
    }

    public static SendMessage sendCustomerDataNotFoundMessage(User telegramUser, CustomerTelegram customerTelegram) {
        ResourceBundle resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramUser.getId());
        sendMessage.setText(resourceBundle.getString(BOT_NOT_FOUND_MESSAGE));
        return sendMessage;
    }

    public static SendMessage sendCustomerDataNotFoundMessageWithParamTelegramUser(User telegramUser) {
        ResourceBundle resourceBundle = getResourceBundleUsingTelegramUser(telegramUser);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramUser.getId());
        sendMessage.setText(resourceBundle.getString(BOT_NOT_FOUND_MESSAGE));
        return sendMessage;
    }

    public static void buildCustomerPayments(PaymentDTO payment, StringBuilder buildCustomerPayments, String languageCode) {
        ResourceBundle resourceBundle = getResourceBundleUsingLanguageCode(languageCode);
        buildCustomerPayments.append(String.format(STRING_FORMAT_ONE_TEXT_ONE_LONG, resourceBundle.getString(BOT_NUMBER_DEBTS),payment.getId()));
        buildCustomerPayments.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_TYPE_SERVICE), payment.getService().getName()));
        buildCustomerPayments.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_ORGANIZATION_NAME), payment.getGroup().getOrganization().getName()));
        buildCustomerPayments.append(String.format(STRING_FORMAT_TWO_TEXT, resourceBundle.getString(BOT_GROUP_NAME), payment.getGroup().getName()));
        buildCustomerPayments.append(String.format(STRING_FORMAT_ONE_TEXT_ONE_FLOAT_WITH_SUM, resourceBundle.getString(BOT_SERVICE_PRICE), payment.getPaymentForPeriod()));
        buildCustomerPayments.append(String.format(STRING_FORMAT_ONE_TEXT_ONE_FLOAT_WITH_SUM, resourceBundle.getString(BOT_PAID_MONEY), payment.getPaidMoney()));

        buildCustomerPayments.append(String.format("""
                <b>%s:</b>
                <b>%s: </b> %s
                <b>%s: </b> %s

                """,
            resourceBundle.getString(BOT_TIME_PAYMENT),
            resourceBundle.getString(BOT_STARTED_TIME_PAYMENT),
            DateUtils.parseToStringFromLocalDate(payment.getStartedPeriod()),
            resourceBundle.getString(BOT_FINISHED_TIME_PAYMENT),
            DateUtils.parseToStringFromLocalDate(payment.getFinishedPeriod())));
    }

    public static void buildCustomerPaymentsToSendEmailMessage(PaymentDTO payment, StringBuilder buildCustomerPayments, String languageCode) {
        ResourceBundle resourceBundle = getResourceBundleUsingLanguageCode(languageCode);
        buildCustomerPayments.append(String.format("%s: %d\n", resourceBundle.getString(BOT_NUMBER_DEBTS),payment.getId()));
        buildCustomerPayments.append(String.format("%s: %s\n", resourceBundle.getString(BOT_TYPE_SERVICE), payment.getService().getName()));
        buildCustomerPayments.append(String.format("%s: %s\n", resourceBundle.getString(BOT_ORGANIZATION_NAME), payment.getGroup().getOrganization().getName()));
        buildCustomerPayments.append(String.format("%s: %s\n", resourceBundle.getString(BOT_GROUP_NAME), payment.getGroup().getName()));
        buildCustomerPayments.append(String.format("%s: %.2f sum\n", resourceBundle.getString(BOT_SERVICE_PRICE), payment.getPaymentForPeriod()));
        buildCustomerPayments.append(String.format("%s: %.2f sum\n", resourceBundle.getString(BOT_PAID_MONEY), payment.getPaidMoney()));

        buildCustomerPayments.append(String.format("""
                %s:
                %s: %s
                %s: %s

                """,
            resourceBundle.getString(BOT_TIME_PAYMENT),
            resourceBundle.getString(BOT_STARTED_TIME_PAYMENT),
            DateUtils.parseToStringFromLocalDate(payment.getStartedPeriod()),
            resourceBundle.getString(BOT_FINISHED_TIME_PAYMENT),
            DateUtils.parseToStringFromLocalDate(payment.getFinishedPeriod())));
    }

    public static InlineKeyboardMarkup paymentOfCustomerInlineMarkupWithLeftAndRightButton(PaymentDTO currentPaymentDTO, Long indexOfLeftPaymentDTO, Long indexOfRightPaymentDTO, CustomerTelegram customerTelegram, String backHomeMenuButton, String dataBackToHome) {
        ResourceBundle resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        InlineKeyboardButton payButton = new InlineKeyboardButton(resourceBundle.getString(BOT_PAY_BUTTON));
        payButton.setCallbackData("payment " + currentPaymentDTO.getId());

        InlineKeyboardButton leftButton = new InlineKeyboardButton("⬅️");
        leftButton.setCallbackData("left " + indexOfLeftPaymentDTO);

        InlineKeyboardButton rightButton = new InlineKeyboardButton("➡️");
        rightButton.setCallbackData("right " + indexOfRightPaymentDTO);

        InlineKeyboardButton backButton = new InlineKeyboardButton(backHomeMenuButton);
        backButton.setCallbackData(dataBackToHome);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(List.of(leftButton, payButton, rightButton), List.of(backButton)));
        return markup;
    }

    public static InlineKeyboardMarkup onlyOnePaymentOfCustomerInlineMarkup(PaymentDTO currentPaymentDTO, CustomerTelegram customerTelegram, String backHomeMenuButton, String dataBackToHome) {
        ResourceBundle resourceBundle = getResourceBundleUsingCustomerTelegram(customerTelegram);
        InlineKeyboardButton payButton = new InlineKeyboardButton(resourceBundle.getString(BOT_PAY_BUTTON));
        payButton.setCallbackData("payment " + currentPaymentDTO.getId());

        InlineKeyboardButton backButton = new InlineKeyboardButton(backHomeMenuButton);
        backButton.setCallbackData(dataBackToHome);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(List.of(payButton), List.of(backButton)));
        return markup;
    }


    public static InlineKeyboardMarkup backToMenuInlineButton(CustomerTelegram customerTelegram, String backHomeMenuButton, String dataBackToHome) {
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton(backHomeMenuButton);
        keyboardButton.setCallbackData(dataBackToHome);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(List.of(keyboardButton)));

        return inlineKeyboardMarkup;
    }
}
