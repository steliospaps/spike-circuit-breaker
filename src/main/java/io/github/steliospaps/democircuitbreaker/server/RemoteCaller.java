package io.github.steliospaps.democircuitbreaker.server;

import io.github.steliospaps.democircuitbreaker.server.dto.Reply;
import reactor.core.publisher.Mono;

public interface RemoteCaller {

	Mono<Reply> execute();

}
