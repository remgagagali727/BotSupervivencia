package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Crafting;
import com.remgagagali727.discord.survplanet.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CraftingRepository extends JpaRepository<Crafting, Crafting.CraftingId> {
    List<Crafting> findByItem(Item item);
}
