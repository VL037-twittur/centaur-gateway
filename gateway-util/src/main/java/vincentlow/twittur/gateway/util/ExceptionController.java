package vincentlow.twittur.gateway.util;

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
import vincentlow.twittur.gateway.model.constant.ExceptionMessage;
import vincentlow.twittur.gateway.model.constant.exception.UnauthorizedException;

@Slf4j
@Component
@Order(-2)
public class ExceptionController extends BaseController implements WebExceptionHandler {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

    HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
    String errorMessage = ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE;

    if (ex instanceof UnauthorizedException) {
      httpStatus = HttpStatus.UNAUTHORIZED;
      errorMessage = ex.getMessage();
    }

    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(httpStatus);
    response.getHeaders()
        .setContentType(MediaType.APPLICATION_JSON);

    try {
      DataBuffer buffer = response.bufferFactory()
          .wrap(convertObjectToJsonBytes(toErrorApiResponse(httpStatus, errorMessage)));
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
