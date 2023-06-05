package vincentlow.twittur.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vincentlow.twittur.base.web.model.response.api.ApiSingleResponse;

@FeignClient("cyclops-account-credential")
public interface AccountCredentialFeignClient {

  @PostMapping(value = "/api/v1/auth/validate-token", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ApiSingleResponse<Boolean>> validateToken(@RequestParam String token);
}
