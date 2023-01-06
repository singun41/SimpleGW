package com.project.simplegw.system.helpers;

import java.time.LocalTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimeValidator implements ConstraintValidator<TimeValid, Object> {
    
    @Override
    public void initialize(TimeValid constraintAnnotation) {}

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;   // @TimeValid를 작성하는 곳에서 null 입력인 경우 통과시킨다. null을 허용하지 않아야 한다면 @NotNull을 추가로 쓰자. @NotBlank 쓰면 Exception 뜸.

        } else {
            if(value instanceof String s) {   // js에서 받는 경우는 문자열이므로
                try {
                    LocalTime.parse(s);
                    return true;

                } catch(Exception e) {
                    return false;
                }
            }

            else if(value instanceof LocalTime time) {   // 테스트 할 때는 LocalTime 객체이므로
                try {
                    LocalTime.from(time);
                    return true;

                } catch(Exception e) {
                    return false;
                }

            } else {
                return false;
            }
        }
    }
}
