package io.github.steliospaps.democircuitbreaker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class GreetingHandler {

  @Autowired
  private RemoteCaller remoteCaller;
  
  @SuppressWarnings("rawtypes")
  @Autowired
  private ReactiveCircuitBreakerFactory cbFactory;
	
  public Mono<ServerResponse> hello(ServerRequest request) {
    return remoteCaller.execute()
    		.transform(it -> cbFactory.create("slow").run(it, t -> Mono.error(t)))
    		.flatMap(
    			reply -> ServerResponse.ok()//
    			.contentType(MediaType.APPLICATION_JSON)//
    			.bodyValue(reply))//
    		.onErrorResume(t -> ServerResponse.status(HttpStatus.BAD_GATEWAY).build())
    		;    		
  }
}

