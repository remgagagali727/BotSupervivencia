package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Spaceship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceshipRepository extends JpaRepository<Spaceship, Long> {
}
