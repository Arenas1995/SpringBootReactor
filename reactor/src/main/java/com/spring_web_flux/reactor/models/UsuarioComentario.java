package com.spring_web_flux.reactor.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UsuarioComentario {

    private Usuario usuario;

    private Comentario comentario;
}
