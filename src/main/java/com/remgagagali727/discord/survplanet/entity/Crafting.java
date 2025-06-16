package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@IdClass(Crafting.CraftingId.class)
public class Crafting {
    @Id
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Id
    @ManyToOne
    @JoinColumn(name = "required_id")
    private Item required;

    private String amount;

    public class CraftingId implements Serializable {
        private Long item;
        private Long required;

        public CraftingId() {}

        public CraftingId(Long item, Long required) {
            this.item = item;
            this.required = required;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CraftingId that)) return false;
            return Objects.equals(item, that.item) &&
                    Objects.equals(required, that.required);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, required);
        }
    }
}
