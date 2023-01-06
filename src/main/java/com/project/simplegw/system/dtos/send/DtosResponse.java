package com.project.simplegw.system.dtos.send;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DtosResponse {
    /*
        각각의 컨트롤러에서 요청에 대한 응답 공통 객체.
        obj에는 메시지 외에 필요한 데이터를 같이 리턴하기 위함.

        예시: 문서등록 성공시 msg: 성공메시지, obj: 문서ID --> view에서 메시지 띄워주고, 문서ID를 다시 호출하면 해당 문서 페이지로 이동.
    */
    private String msg;
    private Object obj;
}
