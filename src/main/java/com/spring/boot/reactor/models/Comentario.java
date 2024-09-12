package com.spring.boot.reactor.models;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comentario {

    private List<String> comentarios;
}
