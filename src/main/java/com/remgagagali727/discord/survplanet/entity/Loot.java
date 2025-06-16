package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Loot {

    @EmbeddedId
    private LootId id;

    @ManyToOne(optional = false)
    @MapsId("planetId")

    private Planet planet;

    @ManyToOne(optional = false)
    @MapsId("itemId")
    private Item item;

    @ManyToOne(optional = false)
    @MapsId("typeId")
    private Type type;

    private String amount;

    @Embeddable
    @Setter
    @Getter
    public class LootId implements Serializable {
        private Long planetId;
        private Long itemId;
        private Long typeId;

        public LootId() {
        }

        public LootId(Long planetId, Long itemId, Long typeId) {
            this.planetId = planetId;
            this.itemId = itemId;
            this.typeId = typeId;
        }

        // equals y hashCode son esenciales para clave compuesta
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LootId)) return false;
            LootId that = (LootId) o;
            return Objects.equals(planetId, that.planetId)
                    && Objects.equals(itemId, that.itemId)
                    && Objects.equals(typeId, that.typeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(planetId, itemId, typeId);
        }
    }
}
