package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.entity.ItemRelation;
import com.remgagagali727.discord.survplanet.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRelationRepository extends JpaRepository<ItemRelation, ItemRelation.ItemRelationId> {
    List<ItemRelation> findByPlayer(Player player);
    Optional<ItemRelation> findByPlayerAndItem(Player player, Item item);
}
