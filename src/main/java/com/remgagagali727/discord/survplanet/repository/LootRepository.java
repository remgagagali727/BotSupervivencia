package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.entity.Loot;
import com.remgagagali727.discord.survplanet.entity.Planet;
import com.remgagagali727.discord.survplanet.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LootRepository extends JpaRepository<Loot, Loot.LootId> {
    List<Loot> findByPlanetAndType(Planet planet, Type type);

    List<Loot> findByPlanet(Planet planet);

    List<Loot> findByItem(Item item);
}
