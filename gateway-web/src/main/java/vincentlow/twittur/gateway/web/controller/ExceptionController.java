package vincentlow.twittur.gateway.web.controller;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import vincentlow.twittur.base.web.controller.BaseController;
import vincentlow.twittur.base.web.model.response.api.ApiResponse;
import vincentlow.twittur.web.model.response.exception.UnauthorizedException;

@Slf4j
@Component
@Order(-2)
public class ExceptionController extends BaseController implements WebExceptionHandler {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

    HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

    if (ex instanceof UnauthorizedException) {
      httpStatus = HttpStatus.UNAUTHORIZED;
    }

    ApiResponse errorResponse = toErrorApiResponse(httpStatus, ex.getMessage());

    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(httpStatus);
    response.getHeaders()
        .setContentType(MediaType.APPLICATION_JSON);

    try {
      DataBuffer buffer = response.bufferFactory()
          .wrap(convertObjectToJsonBytes(errorResponse));
      log.error("ExceptionController#handle ERROR! with error: {}", ex.getMessage());
      return response.writeWith(Mono.just(buffer));
    } catch (JsonProcessingException e) {
      log.error("ExceptionController#convertObjectToJsonBytes ERROR! with error: {}", e.getMessage());
      return response.writeWith(Mono.just(response.bufferFactory()
          .wrap(new byte[0])));
    }
  }

  private byte[] convertObjectToJsonBytes(Object object) throws JsonProcessingException {

    return objectMapper.writeValueAsBytes(object);
  }

}
