package io.github.steliospaps.democircuitbreaker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.github.steliospaps.democircuitbreaker.server.dto.Reply;
import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

  @Autowired
  private RemoteCaller remoteCaller;
	
  public Mono<ServerResponse> hello(ServerRequest request) {
    return remoteCaller.execute().flatMap(
    			reply -> ServerResponse.ok()//
    			.contentType(MediaType.APPLICATION_JSON)//
    			.bodyValue(reply))//
    		.onErrorResume(t -> ServerResponse.status(HttpStatus.BAD_GATEWAY).build());    		
  }
}

