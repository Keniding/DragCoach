package com.keniding.dragcoach.soccer.model;

import lombok.Data;

@Data
public class Player {
    private int id;
    private String nombre;
    private String posicion; // Delantero, Defensa, etc.
    private int numero;
    private String estado; // Titular, Suplente, Lesionado

}
