package com.project.simplegw.approval.entities;

// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.document.entities.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "sgw_approval_namecard", indexes = @Index(name = "sgw_approval_namecard_index_1", columnList = "docs_id"))
public class Namecard extends SubListEntity <Namecard> {
    @Column(name = "team", nullable = false, columnDefinition = Constants.COLUMN_DEFINE_TEAM)
    private String team;

    @Column(name = "job_title", nullable = false, columnDefinition = Constants.COLUMN_DEFINE_JOB_TITLE)
    private String jobTitle;

    @Column(name = "name", nullable = false, columnDefinition = Constants.COLUMN_DEFINE_NAME)
    private String name;

    @Column(name = "name_eng", nullable = false, length = Constants.COLUMN_LENGTH_NAME)   // 영문이름이므로 nvarchar이 아니다. 길이만 설정한다.
    private String nameEng;

    @Column(name = "mail_address", nullable = false, length = Constants.COLUMN_LENGTH_MAIL_ADDRESS)
    private String mailAddress;

    @Column(name = "tel", nullable = false, length = Constants.COLUMN_LENGTH_MOBILE_NO)
    private String tel;

    @Column(name = "mobile_no", nullable = false, length = Constants.COLUMN_LENGTH_MOBILE_NO)
    private String mobileNo;

    @Override
    public Namecard insertDocs(Document docs) {
        this.docs = docs;
        return this;
    }

    // DTO에서 체크한다.
    // public static boolean checkMobileNo(String mobileNo) {
    //     String mobileNoPattern = "\\d{3}-\\d{3,4}-\\d{4}";
    //     Pattern pattern = Pattern.compile(mobileNoPattern);
    //     Matcher matcher = pattern.matcher(mobileNo);
    //     return matcher.matches();
    // }
    // public static boolean checkMailAddress(String mailAddress) {
    //     // \w+@\w+\.+(\.\w+)?  // \w = [a-zA-Z0-9] , + = 한 개 이상 , @ = @ , \. = . , * = 없거나 또는 한 개 이상
    //     String mailAddressPattern = "\\w+@\\w+\\.\\w+(\\.\\w+)*";
    //     Pattern pattern = Pattern.compile(mailAddressPattern);
    //     Matcher matcher = pattern.matcher(mailAddress);
    //     return matcher.matches();
    // }
    // public static boolean checkTel(String tel) {
    //     String telPattern = "\\d{1,4}";   // 1 ~ 4자리 번호
    //     Pattern pattern = Pattern.compile(telPattern);
    //     Matcher matcher = pattern.matcher(tel);
    //     return matcher.matches() || checkMobileNo(tel);   // 두 양식 모두 허용
    // }

    public Namecard updateTeam(String team) {
        this.team = team;
        return this;
    }
    public Namecard updateJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }
    public Namecard updateName(String name) {
        this.name = name;
        return this;
    }
    public Namecard updateNameEng(String nameEng) {
        this.nameEng = nameEng;
        return this;
    }
    public Namecard updateMail(String mailAddress) {
        // if(mailAddress != null && !mailAddress.isBlank() && mailAddress.length() <= ConstantsSystem.COLUMN_LENGTH_MAIL_ADDRESS && checkMailAddress(mailAddress)) {
            this.mailAddress = mailAddress;
        // }
        return this;
    }
    public Namecard updateTel(String tel) {
        // if(tel != null && !tel.isBlank() && tel.length() <= ConstantsSystem.COLUMN_LENGTH_MOBILE_NO && checkTel(tel)) {
            this.tel = tel;
        // }
        return this;
    }
    public Namecard updateMobile(String mobileNo) {
        // if(mobileNo != null && !mobileNo.isBlank() && mobileNo.length() <= ConstantsSystem.COLUMN_LENGTH_MOBILE_NO && checkMobileNo(mobileNo)) {
            this.mobileNo = mobileNo;
        // }
        return this;
    }
}
