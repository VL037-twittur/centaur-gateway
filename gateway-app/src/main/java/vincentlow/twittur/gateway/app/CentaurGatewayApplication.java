package vincentlow.twittur.gateway.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"vincentlow.twittur.gateway.*"})
public class CentaurGatewayApplication {

  public static void main(String[] args) {

    SpringApplication.run(CentaurGatewayApplication.class);
  }
}
