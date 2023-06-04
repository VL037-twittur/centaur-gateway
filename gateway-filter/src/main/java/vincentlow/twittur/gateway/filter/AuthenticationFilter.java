package vincentlow.twittur.gateway.filter;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import vincentlow.twittur.gateway.model.constant.ExceptionMessage;
import vincentlow.twittur.gateway.service.JWTService;
import vincentlow.twittur.web.model.response.exception.UnauthorizedException;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final String STARTS_WITH_BEARER = "Bearer ";

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
      if (!isRequestAllowed(exchange)) {
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

  private static boolean isRequestAllowed(ServerWebExchange exchange) {

    Map<String, HttpMethod> allowedAPIs = Map.of(
        "/api/v1/auth/**", HttpMethod.POST,
        "/api/v1/accounts/**", HttpMethod.GET,
        "/api/v1/tweets/**", HttpMethod.GET);

    AntPathMatcher pathMatcher = new AntPathMatcher();
    String requestURI = exchange.getRequest()
        .getURI()
        .getPath();
    HttpMethod requestMethod = exchange.getRequest()
        .getMethod();

    return allowedAPIs.entrySet()
        .stream()
        .anyMatch(entry -> pathMatcher.match(entry.getKey(), requestURI)
            && entry.getValue()
                .equals(requestMethod));
  }

}
