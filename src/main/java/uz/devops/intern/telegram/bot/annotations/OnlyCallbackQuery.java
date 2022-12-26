package uz.devops.intern.telegram.bot.annotations;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Constraint(validatedBy = {CallbackQueryAnnImpl.class})
public @interface OnlyCallbackQuery {
}
