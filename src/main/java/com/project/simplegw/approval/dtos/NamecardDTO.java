package com.project.simplegw.approval.dtos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class NamecardDTO {
    private String team;
    private String jobTitle;
    private String name;
    private String nameEng;
    private String mailAddress;
    private String tel;
    private String mobileNo;

    public static boolean isValidMobileNo(String mobileNo) {
        String mobileNoPattern = "\\d{3}-\\d{3,4}-\\d{4}";
        Pattern pattern = Pattern.compile(mobileNoPattern);
        Matcher matcher = pattern.matcher(mobileNo);
        return matcher.matches();
    }
    public static boolean isValidMailAddress(String mailAddress) {
        // \w+@\w+\.+(\.\w+)?  // \w = [a-zA-Z0-9] , + = 한 개 이상 , @ = @ , \. = . , * = 없거나 또는 한 개 이상
        String mailAddressPattern = "\\w+@\\w+\\.\\w+(\\.\\w+)*";
        Pattern pattern = Pattern.compile(mailAddressPattern);
        Matcher matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }
    public static boolean isValidTel(String tel) {
        String telPattern = "\\d{1,4}";   // 1 ~ 4자리 번호
        Pattern pattern = Pattern.compile(telPattern);
        Matcher matcher = pattern.matcher(tel);
        return matcher.matches() || isValidMobileNo(tel);   // 두 양식 모두 허용
    }
}
