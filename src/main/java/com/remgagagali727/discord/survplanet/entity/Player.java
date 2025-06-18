package com.remgagagali727.discord.survplanet.entity;

import com.remgagagali727.discord.survplanet.repository.ItemRelationRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setFooter("Survival Universe Planet")
                .setTitle(event.getAuthor().getEffectiveName() + " you are not in a planet!")
                .setDescription("You cannot do this action while in space")
                .setImage("https://cdn.pixabay.com/animation/2022/11/16/14/56/14-56-49-778_512.gif")
                .setColor(Color.BLUE);
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
