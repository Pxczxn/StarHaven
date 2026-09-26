package top.pxczxn.api.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 同一进程再绑管理端端口。官方做法见 Spring Boot How-to
 * “Enable Multiple Connectors with Tomcat”。
 */
@Configuration(proxyBeanMethods = false)
public class AdminPortConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> adminConnectorCustomizer(
            @Value("${starhaven.admin-port:5568}") int adminPort) {
        return tomcat -> tomcat.addAdditionalTomcatConnectors(createConnector(adminPort));
    }

    private Connector createConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        return connector;
    }
}
