//package vincentlow.twittur.gateway.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfiguration {
//
//  @Bean
//  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//
//    return builder.routes()
//        .route(p -> p
//            .path("/auth/**")
//            .uri("lb://cyclops-account-credential/api/v1/auth/**"))
//        .route(p -> p
//            .path("/accounts/**")
//            .uri("lb://gargoyle-account-profile/api/v1/accounts/**"))
//        // .route("cyclops-account-credential", r -> r.path("/api/v1/auth/**")
//        // .uri("lb://cyclops-account-credential"))
//        // .route("gargoyle-account-profile", r -> r.path("/api/v1/accounts/**")
//        // .uri("lb://gargoyle-account-profile"))
//        .build();
//  }
//}
