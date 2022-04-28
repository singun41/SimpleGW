package com.project.simplegw.common.vos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestResult {
    // service 클래스에서 결과여부 및 리턴값을 리턴하기 위한 공용 객체
    private ResultStatus status;
    private String message;
    private Object returnObj;


    // service 클래스에서 리턴값이 없고, 결과여부만 전달할 때 사용
    public static RequestResult getDefaultSuccess(String msg) {
        return builder().status(ResultStatus.SUCCESS).message(msg).returnObj(null).build();
    }
    public static RequestResult getDefaultFail(String msg) {
        return builder().status(ResultStatus.FAIL).message(msg).returnObj(null).build();
    }
    public static RequestResult getDefaultError(String msg) {
        return builder().status(ResultStatus.ERROR).message(msg).returnObj(null).build();
    }
}
