package com.project.simplegw.system.config;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomizingTomcatFactory implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    public CustomizingTomcatFactory() {
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // Rest Api 작성시 PUT, PATCH 메서드의 requestBody를 추가함. 기본값은 POST만 설정되어 있음.
        TomcatConnectorCustomizer parseBodyMethodCustomizer = connector -> {
            String useBodyMethods = "POST,PUT,PATCH";
            connector.setParseBodyMethods(useBodyMethods);
            log.info("Change the 'Request Body' method to {}.", useBodyMethods);
        };
        factory.addConnectorCustomizers(parseBodyMethodCustomizer);
    }
}
