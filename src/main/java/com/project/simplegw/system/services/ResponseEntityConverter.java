package com.project.simplegw.system.services;

import com.project.simplegw.common.vos.RequestResult;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityConverter {
    // service 클래스에서 리턴받은 결과를 ResponseEntity로 변환하는 공용메서드. controller 클래스에서 사용.
    public static ResponseEntity<Object> getFromRequestResult(RequestResult result) {
        ResponseEntity<Object> response = null;
        switch(result.getStatus()) {
            case SUCCESS:
                response = new ResponseEntity<>(result, HttpStatus.OK);
                break;
            case FAIL:
                response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
                break;
            case ERROR:
                response = new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
                break;
        }
        return response;
    }
}
