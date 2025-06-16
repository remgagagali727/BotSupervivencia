package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@NoArgsConstructor
public class Drill{
    @Id
    private Long id;
    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Item item;
    private String toughness;
}
