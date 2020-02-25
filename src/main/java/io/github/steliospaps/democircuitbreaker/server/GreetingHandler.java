package io.github.steliospaps.democircuitbreaker.server;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.github.steliospaps.democircuitbreaker.server.dto.Reply;
import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

  public Mono<ServerResponse> hello(ServerRequest request) {
    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(new Reply("Hello, Spring!")));
  }
}

