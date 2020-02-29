package io.github.steliospaps.democircuitbreaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class DemoCircuitBreakerApplication {

	public static void main(String[] args) {
		Schedulers.enableMetrics();
		SpringApplication.run(DemoCircuitBreakerApplication.class, args);
	}
	

}
