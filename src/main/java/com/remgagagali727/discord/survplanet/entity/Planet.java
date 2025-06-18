package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode
public class Planet{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String toughness;
    private String x;
    private String y;
    @OneToMany
    private List<Player> players;
    @OneToMany
    private List<Loot> loots;
}
