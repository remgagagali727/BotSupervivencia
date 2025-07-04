package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanetRepository extends JpaRepository<Planet, Long> {
    Optional<Planet> findByName(String name);

    @Query(value = "SELECT * FROM planet ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Planet> findRandomPlanet();

    Optional<Planet> findByNameIgnoreCase(String name);
}
