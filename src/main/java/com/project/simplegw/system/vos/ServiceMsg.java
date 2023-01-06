package com.project.simplegw.system.vos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ServiceMsg {
    // 서비스에서 컨트롤러에 전달할 데이터들을 공통으로 묶은 클래스

    private ServiceResult result;
    private Object returnObj;   // 서비스에서 리턴해야 하는 객체를 바인딩
    private String msg;   // 서비스에서 디테일하게 정의한 메시지를 바인딩
}
