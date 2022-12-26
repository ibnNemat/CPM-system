package uz.devops.intern.telegram.bot.annotations;

import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MessageAnnImpl implements ConstraintValidator<OnlyMessage, Update> {
    @Override
    public void initialize(OnlyMessage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Update value, ConstraintValidatorContext context) {
        return value.hasMessage();
    }
}
