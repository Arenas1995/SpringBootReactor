package com.spring_web_flux.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactorApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(SpringApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Flux<String> nombres = Flux.just("Mateo", "Kate", "Migue")
				.doOnNext(elemento -> {
					if (elemento.isEmpty()) {
						throw new RuntimeException("nombres no puede ser vacio");
					}
					System.out.println(elemento);
				});

		nombres.subscribe(LOG::info, error -> LOG.error(error.getMessage()),
                () -> LOG.info("Finalizo la ejecucion del observable"));
	}
}
