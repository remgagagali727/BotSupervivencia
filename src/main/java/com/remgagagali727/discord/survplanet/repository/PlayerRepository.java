package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

}
