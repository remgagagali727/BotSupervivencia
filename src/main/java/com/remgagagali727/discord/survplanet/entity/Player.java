package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player {
    @Id
    private Long id;
    private String coins;
    @ManyToOne
    @JoinColumn(name = "e_drill")
    private Drill drill;
    @ManyToOne
    @JoinColumn(name = "e_rod")
    private Rod rod;
    @ManyToOne
    @JoinColumn(name = "e_weapon")
    private Weapon weapon;
    @ManyToOne
    @JoinColumn(name = "e_spaceship")
    private Spaceship spaceship;
    private LocalDateTime n_fish;
    private LocalDateTime n_mine;
    private LocalDateTime n_hunt;
    private String health;

    public Player(Long id) {
        this.id = id;
    }
}
