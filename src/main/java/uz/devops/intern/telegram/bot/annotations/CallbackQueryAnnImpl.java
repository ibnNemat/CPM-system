package uz.devops.intern.telegram.bot.annotations;

import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CallbackQueryAnnImpl implements ConstraintValidator<OnlyCallbackQuery, Update> {
    @Override
    public void initialize(OnlyCallbackQuery constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Update value, ConstraintValidatorContext context) {
        return value.hasCallbackQuery();
    }
}
