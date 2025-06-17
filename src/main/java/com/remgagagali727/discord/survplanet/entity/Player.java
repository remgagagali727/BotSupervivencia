package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;
import java.util.List;

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
    private String maxHealth;
    @ManyToOne
    @JoinColumn(name = "id_planet")
    private Planet planet;
    private LocalDateTime arrive;
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemRelation> inventory;


    public Player(Long id) {
        this.id = id;
    }

    public boolean isOnPlanet() {
        return !arrive.isAfter(LocalDateTime.now());
    }

    public void notInPlanet(MessageReceivedEvent event) {
        event.getChannel().sendMessage("You will arrive to the planet " + planet.getName() + " at " + arrive).queue();
    }
}
