package com.spring.boot.reactor.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Usuario {

    private String nombre;
    private String apellido;
}
