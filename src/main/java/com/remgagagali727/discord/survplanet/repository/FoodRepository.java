package com.remgagagali727.discord.survplanet.repository;

import com.remgagagali727.discord.survplanet.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    @Query("SELECT f FROM Food f JOIN f.item i WHERE LOWER(i.name) LIKE LOWER(concat('%', ?1, '%'))")
    List<Food> findByItemNameContaining(String name);
    boolean existsById(Long itemId);
    @Query("SELECT f FROM Food f JOIN f.item i WHERE LOWER(i.name) = LOWER(:name)")
    Optional<Food> findByExactName(@Param("name") String name);
    

}