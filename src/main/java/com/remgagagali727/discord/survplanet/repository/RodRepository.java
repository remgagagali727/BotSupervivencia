package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Rod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RodRepository extends JpaRepository<Rod, Long> {

}
