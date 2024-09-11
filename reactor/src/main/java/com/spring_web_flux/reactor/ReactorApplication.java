package com.spring_web_flux.reactor;

import com.spring_web_flux.reactor.models.Usuario;
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

		Flux<String> nombres = Flux.just("Mateo Arenas", "Kate Gomez", "Miguel Angel", "Oscar Duvan");
		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),
						nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().equalsIgnoreCase("mateo"))
				.doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("nombres no puede ser vacio");
					}
					System.out.println(usuario);
				}).map(usuario -> {
					String nombre = usuario.getNombre().toUpperCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(elemento -> LOG.info(elemento.toString()), error -> LOG.error(error.getMessage()),
                () -> LOG.info("Finalizo la ejecucion del observable"));
	}
}
