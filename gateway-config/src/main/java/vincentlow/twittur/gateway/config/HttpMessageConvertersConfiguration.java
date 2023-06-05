package vincentlow.twittur.gateway.config;

import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class HttpMessageConvertersConfiguration {

  @Bean
  public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {

    return new HttpMessageConverters(converters.orderedStream()
        .collect(Collectors.toList()));
  }
}
