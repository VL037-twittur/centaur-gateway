package vincentlow.twittur.gateway.filter;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import vincentlow.twittur.gateway.model.constant.ExceptionMessage;
import vincentlow.twittur.gateway.service.JWTService;
import vincentlow.twittur.web.model.response.exception.UnauthorizedException;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final String STARTS_WITH_BEARER = "Bearer ";

  @Autowired
  private RouteValidator routeValidator;

  @Autowired
  private JWTService jwtService;

  public static class Config {
  }

  public AuthenticationFilter() {

    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {

    return ((exchange, chain) -> {
      if (routeValidator.isSecured.test(exchange.getRequest())) {

        HttpHeaders headers = exchange.getRequest()
            .getHeaders();
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
          return Mono.error(new UnauthorizedException(ExceptionMessage.AUTHORIZATION_HEADER_IS_MISSING));
        }

        String authHeader = headers.get(HttpHeaders.AUTHORIZATION)
            .get(0);

        if (Objects.nonNull(authHeader) && authHeader.startsWith(STARTS_WITH_BEARER)) {
          authHeader = authHeader.substring(STARTS_WITH_BEARER.length());
        }

        try {
          jwtService.validateToken(authHeader);
        } catch (Exception e) {
          return Mono.error(new UnauthorizedException(ExceptionMessage.INVALID_OR_EXPIRED_TOKEN));
        }
      }
      return chain.filter(exchange);
    });
  }

}
