package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
    @OneToMany(fetch = FetchType.EAGER)
    private List<Loot> loots;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Planet o)) return false;
        return o.name.equals(name);
    }
}
