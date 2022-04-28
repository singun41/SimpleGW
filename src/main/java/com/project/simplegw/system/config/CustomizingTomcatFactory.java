package com.project.simplegw.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class CustomizingTomcatFactory implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // Rest Api 작성시 PUT, PATCH 메서드의 requestBody를 추가함. 기본값은 POST만 설정되어 있음.
        TomcatConnectorCustomizer parseBodyMethodCustomizer = connector -> {
            String useBodyMethods = "POST,PUT,PATCH";
            connector.setParseBodyMethods(useBodyMethods);
            logger.info("requestBody를 사용하는 메서드를 기본값에서 {} 으로 커스터마이징 합니다.", useBodyMethods);
        };
        
        factory.addConnectorCustomizers(parseBodyMethodCustomizer);
    }
}
