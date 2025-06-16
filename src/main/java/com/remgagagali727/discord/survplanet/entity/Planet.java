package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Planet o)) return false;
        return o.name.equals(name);
    }
}
