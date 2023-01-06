package com.project.simplegw.system.helpers;

import java.lang.annotation.Documented;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = TimeValidator.class)
public @interface TimeValid {
    String message() default "시간 또는 형식이 올바르지 않습니다. (00:00 ~ 23:59)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
