package com.project.simplegw.system.helpers;

import com.project.simplegw.system.dtos.send.DtosResponse;
import com.project.simplegw.system.vos.ResponseMsg;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.system.vos.ServiceMsg;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public class ResponseConverter {

    // 메시지와 객체 둘 다 리턴할 때, 예시: 문서 생성시 생성 성공여부 메시지와 성공시 문서번호를 리턴
    public static ResponseEntity<Object> message(ServiceMsg serviceMsg, ResponseMsg responseMsg) {
        if(serviceMsg.getResult() == ServiceResult.SUCCESS)
            return ResponseEntity.ok().body( new DtosResponse().setMsg(responseMsg.getTitle()).setObj(serviceMsg.getReturnObj()) );
        
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new DtosResponse().setMsg(serviceMsg.getMsg()) );
    }


    
    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 200 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    // 메시지 없이 객체만 리턴할 때, 예시: 게시판에서 리스트 불러오기, 게시글의 코멘트 리스트 불러오기
    public static ResponseEntity<Object> ok(Object obj) {
        return ResponseEntity.ok().body( new DtosResponse().setObj(obj) );
    }

    // 객체 없이 메시지만 리턴할 때, 예시: 문서의 내용 업데이트 시 결과 메시지만 리턴
    public static ResponseEntity<Object> ok(ResponseMsg msg) {
        return ResponseEntity.ok().body( new DtosResponse().setMsg(msg.getTitle()) );
    }

    // 메시지, 객체 모두 없이 상태코드만 리턴.
    public static ResponseEntity<Object> ok() {
        return ResponseEntity.ok().build();
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 200 ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //


    


    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 4xx ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public static ResponseEntity<Object> badRequest(BindingResult result) {   // 수신전용 dto의 validation error 리턴
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new DtosResponse().setMsg(result.getAllErrors().stream().findFirst().get().getDefaultMessage())
        );
    }

    public static ResponseEntity<Object> unauthorized() {   // 컨트롤러 단에서 바로 권한없음 리턴
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new DtosResponse().setMsg(ResponseMsg.UNAUTHORIZED.getTitle())
        );
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- 4xx ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
