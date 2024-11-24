package com.keniding.dragcoach.soccer.model;

import lombok.Data;

@Data
public class PlayerPosition {
    private int number;
    private float x, y;
    private String position;

    public PlayerPosition(int number, float x, float y, String position) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.position = position;
    }
}
