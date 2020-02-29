package io.github.steliospaps.democircuitbreaker;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

@Component
@ConfigurationProperties(prefix = "load-generator")
@Setter
@Slf4j
public class LoadGeneratorCommandLineRunner implements CommandLineRunner {

	private int connectionCount=1;
	
	private int iterrations=0;

	private int delayMs=10;

	/**
	 * add one connection every so many msec until the connection count is reached
	 */
	private int rampUpDelayMs=10;
	
	private String url="http://localhost:8080/hello";

	@Override
	public void run(String... args) throws Exception {
		
		WebClient client = WebClient.create(url);
		
		Flux.interval(Duration.ofMillis(rampUpDelayMs))
			.take(connectionCount)//
			.flatMap(connection -> 
				Flux.interval(Duration.ofMillis(delayMs))
				.onBackpressureBuffer()//
				.take(iterrations)//
				.map(i -> System.nanoTime())
				.flatMap(ts -> client.get()//
						.exchange()//
						.flatMap(r -> r.toBodilessEntity()//
								.map(re -> !re.getStatusCode().isError()))
						.onErrorReturn(false)//
						.map(isSuccess -> Tuples.of(System.nanoTime() - ts, isSuccess))//
						,1)
					,connectionCount)//
			.buffer(Duration.ofSeconds(1))//
			.map(l -> l.stream().collect(Collectors.groupingBy(r -> r.getT2(), 
					Collectors.mapping(r->r.getT1(),
							Collectors.toList()))))// success
			.doOnNext(m -> log.info("good count: {} latency: {} msec errors: count: {} latency:{}",
					Optional.ofNullable(m.get(true)).map(Collection::size).orElse(0),
					Optional.ofNullable(m.get(true)).map(l -> average(l)/1_000_000).orElse(Double.NaN),
					Optional.ofNullable(m.get(false)).map(Collection::size).orElse(0),
					Optional.ofNullable(m.get(false)).map(l -> average(l)/1_000_000).orElse(Double.NaN)

					))//
			.then()//
			.block();
	}

	private double average(List<Long> l) {
		return l.parallelStream().mapToDouble(i -> (double) i)
				.average().orElse(Double.NaN);
	}

}
