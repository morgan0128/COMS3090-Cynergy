package User_Info.socket_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class Socket_Config {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
            return new ServerEndpointExporter();
        }

}