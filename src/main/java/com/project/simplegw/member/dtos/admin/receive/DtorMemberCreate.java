package com.project.simplegw.member.dtos.admin.receive;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.project.simplegw.system.helpers.DateValid;
import com.project.simplegw.system.vos.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString(exclude = "pw")
public class DtorMemberCreate {
    // 관리자가 계정 생성시 사용: 최소 정보만 입력
    
    // DtoMember, DtoMemberDetails로 객체를 직접 멤버변수로 사용하면 컨트롤러에서 @Validated로 체크가 안 된다.

    @NotBlank(message = "아이디를 입력하세요.")
    @Size(min = 4, max = Constants.COLUMN_LENGTH_USER_ID, message = "아이디를 4~" + Constants.COLUMN_LENGTH_USER_ID + "자 길이로 작성하세요.")
    private String id;

    @NotBlank(message = "패스워드를 입력하세요.")
    @Size(min = Constants.PW_UPDATE_AT_LEAST_LENGTH, message = "패스워드를 최소 " + Constants.PW_UPDATE_AT_LEAST_LENGTH + "자 이상, 영문, 숫자, 특수문자를 포함해 작성하세요.")
    @Pattern(regexp = Constants.REGEXP_PW, message = "패스워드를 최소 " + Constants.PW_UPDATE_AT_LEAST_LENGTH + "자 이상, 영문, 숫자, 특수문자를 포함해 작성하세요.")
    private String pw;

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
    private String dateHire;

    @DateValid   // null 허용
    private String birthday;
}
