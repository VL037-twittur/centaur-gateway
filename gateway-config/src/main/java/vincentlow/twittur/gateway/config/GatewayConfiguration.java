package vincentlow.twittur.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {

    return builder.routes()
        .route("cyclops-account-credential", p -> p
            .path("/auth/**")
            .filters(f -> f.rewritePath("/auth/(?<path>.*)", "/api/v1/auth/${path}"))
            .uri("lb://cyclops-account-credential"))
        .route("gargoyle-account-profile", p -> p
            .path("/accounts/**")
            .filters(f -> f.rewritePath("/accounts/(?<path>.*)", "/api/v1/accounts/${path}"))
            .uri("lb://gargoyle-account-profile"))
        .build();
  }
}
