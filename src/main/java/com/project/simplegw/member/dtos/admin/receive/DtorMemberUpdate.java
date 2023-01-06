package com.project.simplegw.member.dtos.admin.receive;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.project.simplegw.system.helpers.DateValid;
import com.project.simplegw.system.helpers.Enum;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DtorMemberUpdate {
    // 관리자가 계정 정보 업데이트 시 사용: 패스워드는 분리.

    @NotBlank
    @Enum(enumClass = Role.class, ignoreCase = true, message = "권한을 지정하세요.")
    private String role;

    private boolean enabled;
    
    @NotBlank(message = "부서를 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_TEAM, message = "부서명을 " + Constants.COLUMN_LENGTH_TEAM + "자 이하로 작성하세요.")
    private String team;

    @NotBlank(message = "직위를 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_JOB_TITLE, message = "직위명을 " + Constants.COLUMN_LENGTH_JOB_TITLE + " 자 이하로 작성하세요.")
    private String jobTitle;

    @NotBlank(message = "이름을 입력하세요.")
    @Size(max = Constants.COLUMN_LENGTH_NAME, message = "이름을 " + Constants.COLUMN_LENGTH_NAME + " 자 이하로 작성하세요.")
    private String name;

    // 아래 필드는 필수가 아닌 옵션, null 허용이 되는 valid로 작성.
    @Size(max = Constants.COLUMN_LENGTH_NAME, message = "영문 이름을 " + Constants.COLUMN_LENGTH_NAME + " 자 이하로 작성하세요.")
    private String nameEng;

    @Pattern(regexp = Constants.REGEXP_MOBILE_NO, message = "핸드폰 번호를 000-0000-0000 형식으로 입력하세요.")
    private String mobile;

    @Pattern(regexp = "\\d{1,4}", message = "내선번호를 1~4자리로 입력하세요.")
    private String tel;

    @Pattern(regexp = Constants.REGEXP_EMAIL, message = "이메일 주소를 정확히 입력하세요.")
    private String email;
    private boolean emailUse;

    @DateValid   // null 허용
    private LocalDate birthday;

    @DateValid   // null 허용
    private LocalDate dateHire;

    @DateValid   // null 허용
    private LocalDate dateResign;

    private boolean resigned;
}
