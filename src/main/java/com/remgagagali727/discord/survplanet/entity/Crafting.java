package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class Crafting {

    @EmbeddedId
    private CraftingId id;

    @MapsId("item")
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @MapsId("required")
    @ManyToOne
    @JoinColumn(name = "required_id")
    private Item required;

    private String amount;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CraftingId implements Serializable {
        private Long item;
        private Long required;
    }
}

