// package com.project.simplegw.system.config;

// import java.util.concurrent.TimeUnit;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.CacheControl;
// import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// // @Configuration
// public class WebConfig implements WebMvcConfigurer {
    
//     // 웹앱 환경설정 클래스
//     // application.properties에서 설정하는 방법이 deprecated 되었으며, WebMvcConfigurer를 구현하도록 변경됨.
//     // 다시 찾아보니 spring.web.resources.chain.cache로 설정이 가능해서 properties에서 적용함.
//     // bean 등록만 하지 않도록 처리함.

//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         // 정적 리소스에 대한 캐시 설정
//         CacheControl cacheControl = CacheControl.maxAge(12, TimeUnit.HOURS);
//         registry.addResourceHandler("**/*.*").addResourceLocations("classpath:/static/").setCacheControl(cacheControl);
//     }
// }
