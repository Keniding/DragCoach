package com.keniding.dragcoach.soccer.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Formation {
    private String name;
    private List<PlayerPosition> homePlayers;
    private List<PlayerPosition> awayPlayers;

    public Formation(String name) {
        this.name = name;
        this.homePlayers = new ArrayList<>();
        this.awayPlayers = new ArrayList<>();
    }
}
