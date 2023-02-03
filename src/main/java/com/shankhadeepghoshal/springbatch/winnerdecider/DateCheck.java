package com.shankhadeepghoshal.springbatch.winnerdecider;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:shankhadeepghoshal1996@gmail.com">Shankhadeep Ghoshal</a>
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface DateCheck {
  String message() default "{date.invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
