package com.project.simplegw.system.vos;

public enum ServiceResult {
    /*
        서비스에서 비즈니스 로직 성공/실패 여부를 컨트롤러에 전달할 때 사용하는 클래스
    
        컨트롤러에 결과를 보낼 때 ServiceMsg 클래스에 이 데이터를 바인딩해서 보낸다.
    */
    
    SUCCESS, FAILURE;
}
