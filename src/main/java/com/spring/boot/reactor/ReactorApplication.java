package com.spring.boot.reactor;

import com.spring.boot.reactor.models.Usuario;
import com.spring.boot.reactor.models.UsuarioComentario;
import com.spring.boot.reactor.models.Comentario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class ReactorApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(SpringApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//ejemploIterable();
		//ejemploFlatMap();
		//ejemploToString();
		//ejemploToCollectList();
		//ejemploUsuariocomentarioFlatMap();
		//ejemploUsuarioComentarioZipWith();
		//ejemploUsuarioComentarioZipWithForma2();
		//ejemploZipWithRango();
		//ejemploIntervalo();
		//ejemploDelayElements();
		//ejemploIntervaloInfinito();
		//ejemploIntervaloDesdeCreate();
		ejemploContrapresion();
	}

	public void ejemploIterable() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Mateo Arenas");
		usuariosList.add("Kate Gomez");
		usuariosList.add("Miguel Angel");
		usuariosList.add("Oscar Duvan");

		Flux<String> nombres = Flux.fromIterable(usuariosList);
		//Flux<String> nombres = Flux.just("Mateo Arenas", "Kate Gomez", "Miguel Angel", "Oscar Duvan");
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

	public void ejemploFlatMap() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Mateo Arenas");
		usuariosList.add("Kate Gomez");
		usuariosList.add("Miguel Angel");
		usuariosList.add("Oscar Duvan");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),
						nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if (usuario.getNombre().equalsIgnoreCase("Mateo")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toUpperCase();
					usuario.setNombre(nombre);
					return usuario;
				})
				.subscribe(usuario -> LOG.info(usuario.toString()));
	}

	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Mateo", "Arenas"));
		usuariosList.add(new Usuario("Kate", "Gomez"));
		usuariosList.add(new Usuario("Miguel", "Angel"));
		usuariosList.add(new Usuario("Mateo", "Duvan"));

		Flux.fromIterable(usuariosList)
				.map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if (nombre.contains("Mateo".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}
				})
				.map(nombre -> nombre)
				.subscribe(LOG::info);
	}

	public void ejemploToCollectList() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Mateo", "Arenas"));
		usuariosList.add(new Usuario("Kate", "Gomez"));
		usuariosList.add(new Usuario("Miguel", "Angel"));
		usuariosList.add(new Usuario("Mateo", "Duvan"));

		Flux.fromIterable(usuariosList)
				.collectList()
				.subscribe(lista -> {
					lista.forEach(item -> LOG.info(item.toString()));
				});
	}

	public void ejemploUsuarioComentarioFlatMap() throws Exception {

		Mono<Usuario> usuarioMono = Mono.fromCallable(this::crearUsuario);
		Mono<Comentario> comentarioMono = Mono.fromCallable(this::crearComentario);

		usuarioMono.flatMap(usuario -> comentarioMono
				.map(comentarios -> new UsuarioComentario(usuario, comentarios)))
				.subscribe(usuarioComentario -> LOG.info(usuarioComentario.toString()));
	}

	public void ejemploUsuarioComentarioZipWith() throws Exception {

		Mono<Usuario> usuarioMono = Mono.fromCallable(this::crearUsuario);
		Mono<Comentario> comentarioMono = Mono.fromCallable(this::crearComentario);

		Mono<UsuarioComentario> usuarioConComentario =  usuarioMono.zipWith(comentarioMono, UsuarioComentario::new);
		usuarioConComentario.subscribe(usuarioComentario -> LOG.info(usuarioComentario.toString()));
	}

	public void ejemploUsuarioComentarioZipWithForma2() throws Exception {

		Mono<Usuario> usuarioMono = Mono.fromCallable(this::crearUsuario);
		Mono<Comentario> comentarioMono = Mono.fromCallable(this::crearComentario);

		Mono<UsuarioComentario> usuarioConComentario =  usuarioMono.zipWith(comentarioMono)
				.map(tuple -> {
					Usuario usuario = tuple.getT1();
					Comentario comentario = tuple.getT2();
					return new UsuarioComentario(usuario, comentario);
				});
		usuarioConComentario.subscribe(usuarioComentario -> LOG.info(usuarioComentario.toString()));
	}

	public void ejemploZipWithRango() throws Exception {

		Flux.just(1, 2, 3, 4)
				.map(i -> (i * 2))
				.zipWith(Flux.range(0, 4), (uno, dos) ->
						String .format("Primer Flux: %d, Segundo Flux: %d", uno, dos))
				.subscribe(LOG::info);
	}

	public void ejemploIntervalo() throws Exception {

		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

		rango.zipWith(retraso, (ra, re) -> ra)
				.doOnNext(i -> LOG.info(i.toString()))
				.subscribe();
	}

	public void ejemploDelayElements() throws Exception {

		Flux<Integer> rango = Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> LOG.info(i.toString()));
		rango.subscribe();
	}

	public void ejemploIntervaloInfinito() throws Exception {

		Flux.interval(Duration.ofSeconds(1))
				.flatMap(i -> {
					if (i >= 5) {
						return Flux.error(new InterruptedException("Solo hasta 5"));
					} else {
						return Flux.just(i);
					}
				})
				.map(i -> "Hola " + i)
				.retry(2)
				.subscribe(LOG::info, e -> LOG.error(e.getMessage()));
	}

	public void ejemploIntervaloDesdeCreate() throws Exception {

		Flux.create(emitter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				private Integer contador = 0;
				@Override
				public void run() {
					emitter.next(++contador);
					if (contador == 10) {
						timer.cancel();
						emitter.complete();
					}

					if (contador == 5) {
						timer.cancel();
						emitter.error(new InterruptedException("Error, se ha detenido el flux."));
					}
				}
			}, 1000, 1000);
		})
				.subscribe(next -> LOG.info(next.toString()), error -> LOG.error(error.getMessage()),
						() -> LOG.info("Finalizo"));
	}

	public void ejemploContrapresion() throws Exception {

		Flux.range(1, 10)
				.log()
				.limitRate(2)
				.subscribe(/*new Subscriber<>() {

					private Subscription subscription;
					private final Integer limite = 2;
					private Integer consumido = 0;

                    @Override
                    public void onSubscribe(Subscription subscription) {
						this.subscription = subscription;
						subscription.request(limite);
                    }

                    @Override
                    public void onNext(Integer integer) {
						LOG.info(integer.toString());
						consumido++;

						if (consumido.equals(limite)) {
							consumido = 0;
							subscription.request(limite);
						}
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }*/);
	}

	public Usuario crearUsuario() {
		return new Usuario("Mateo", "Arenas");
	}

	public Comentario crearComentario() {
		return new Comentario(List.of("Comentario 1", "Comentario 2"));
	}
}
