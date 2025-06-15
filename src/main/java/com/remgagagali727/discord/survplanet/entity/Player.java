package com.remgagagali727.discord.survplanet.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Player {
    private String id;
    private String coins;
    private Drill e_drill;
    private Rod e_rod;
    private Weapon e_weapon;
    private LocalDateTime n_fish;
    private LocalDateTime n_mine;
    private LocalDateTime n_hunt;
    private Spaceship e_spaceship;
    private String health;
}
