package vincentlow.twittur.gateway.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import vincentlow.twittur.base.web.model.response.api.ApiSingleResponse;
import vincentlow.twittur.gateway.client.AccountCredentialFeignClient;
import vincentlow.twittur.gateway.model.constant.ExceptionMessage;
import vincentlow.twittur.gateway.model.constant.exception.UnauthorizedException;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final AccountCredentialFeignClient accountCredentialFeignClient;

  public static class Config {
  }

  @Autowired
  public AuthenticationFilter(@Lazy AccountCredentialFeignClient accountCredentialFeignClient) {

    super(Config.class);
    this.accountCredentialFeignClient = accountCredentialFeignClient;
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

        ResponseEntity<ApiSingleResponse<Boolean>> authResponse =
            accountCredentialFeignClient.validateToken(headers.get(HttpHeaders.AUTHORIZATION)
                .get(0));
        boolean isValid = authResponse.getBody()
            .getData();

        if (!isValid) {
          return Mono.error(new UnauthorizedException(ExceptionMessage.INVALID_OR_EXPIRED_TOKEN));
        }
      }
      return chain.filter(exchange);
    });
  }

  private boolean isRequestAllowed(ServerWebExchange exchange) {

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
