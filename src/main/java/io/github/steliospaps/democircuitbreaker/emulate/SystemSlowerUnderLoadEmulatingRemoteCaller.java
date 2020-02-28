package io.github.steliospaps.democircuitbreaker.emulate;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.steliospaps.democircuitbreaker.server.RemoteCaller;
import io.github.steliospaps.democircuitbreaker.server.dto.Reply;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import reactor.core.publisher.Mono;

@Component
public class SystemSlowerUnderLoadEmulatingRemoteCaller implements RemoteCaller {

	/**
	 * after this limit the latency goes up
	 */
	@Value("${remote.limit-one:100}")
	private int limitOne;

	@Value("${remote.latency-base-ms:500}")
	private int latencyBaseMsec;

	/**
	 * after this limit requests fail (but not immediatelly)
	 */
	@Value("${remote.limit-two:200}")
	private int limitTwo;
	
	private final AtomicInteger inProgress = Metrics.gauge("remote.in-progress", 
			new AtomicInteger());

	/**
	 * latency goes up so many msec per request over limit1
	 */
	@Value("${remote.latency-multiplier:21}")
	private double latencyMultiplier;
	
	@Override
	public Mono<Reply> execute() {
		int currentInProgress = inProgress.incrementAndGet();
		/*
		 * simulate a system that gets slower with load over a specific point,
		 * and then times out internally
		 */
		long latency = Math.round(latencyBaseMsec+latencyMultiplier*Math.max(0,
				Math.min(currentInProgress,limitTwo) - limitOne));
		return Mono.delay(Duration.ofMillis(latency))//
				.flatMap( i ->
					currentInProgress>limitTwo ? 
							Mono.error(() -> new RuntimeException("timeout")) :
								Mono.just(new Reply("hello world")))
				.doFinally(s -> inProgress.decrementAndGet());
	}

}
