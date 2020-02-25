package io.github.steliospaps.democircuitbreaker;

import java.time.Duration;
import java.util.Collection;
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

@Component
@ConfigurationProperties(prefix = "load-generator")
@Setter
@Slf4j
public class LoadGeneratorCommandLineRunner implements CommandLineRunner {

	private int connectionCount=1;
	
	private int iterrations=0;

	private int delayMs=10;
	
	private String url="http://localhost:8080/hello";

	@Override
	public void run(String... args) throws Exception {
		
		WebClient client = WebClient.create(url);
		
		Flux.interval(Duration.ofMillis(delayMs))//
			.take(iterrations)//
			.flatMap(i -> Flux.fromStream(IntStream.rangeClosed(1, connectionCount).boxed())
					.flatMap(j -> client.get().exchange(),connectionCount)//
					)//
			.buffer(Duration.ofSeconds(10))//
			.map(l -> l.stream().collect(Collectors.groupingBy(r -> r.statusCode().is2xxSuccessful(), 
					Collectors.toList())))//
			.doOnNext(m -> log.info("2xx count: {} errors:{}",
					Optional.ofNullable(m.get(true)).map(Collection::size).orElse(0),
					Optional.ofNullable(m.get(false)).map(Collection::size).orElse(0)
					))//
			.then()//
			.block();
	}

}
