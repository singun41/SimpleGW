package com.project.simplegw.system.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PwEncoder extends BCryptPasswordEncoder {
    /*
        객체 순환참조 때문에 분리한다.

        SecurityConfig 클래스에서 @Bean으로 등록해서 사용하려면
        MemberService의 생성자 메서드에서 파라미터로 넣지 않고 따로 @Autowired 해야 한다.
        
        아니면 이렇게 별도 빈으로 만들어서 사용한다.


        - 참고 -
        SecurityConfig에서 PasswordEncoderFactories.createDelegatingPasswordEncoder() 메서드로 받은 객체에서 encode를 실행하면
        패스워드 해싱 문자열 앞에 {bcrypt} 라는 접두사가 붙는다.
        근데 BcryptPasswordEncoder를 이용해서 직접 encode를 실행하면 접두사가 붙지 않는다.
    */
}
