package com.remgagagali727.discord.survplanet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ItemRelation {

    @EmbeddedId
    private ItemRelationId id;

    @ManyToOne(optional = false)
    @MapsId("playerId")
    private Player player;

    @ManyToOne(optional = false)
    @MapsId("itemId")
    private Item item;

    private String amount;

    public ItemRelation(Player player, Item item, String amount) {
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.id = new ItemRelationId(player.getId(), item.getId());
    }

    @Embeddable
    @Getter
    @Setter
    public static class ItemRelationId implements Serializable {
        private Long playerId;
        private Long itemId;

        public ItemRelationId() {

        }

        public ItemRelationId(Long playerId, Long itemId) {
            this.playerId = playerId;
            this.itemId = itemId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemRelationId)) return false;
            ItemRelationId that = (ItemRelationId) o;
            return Objects.equals(playerId, that.playerId) &&
                    Objects.equals(itemId, that.itemId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerId, itemId);
        }
    }
}
