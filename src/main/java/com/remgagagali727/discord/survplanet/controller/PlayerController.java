package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Drill;
import com.remgagagali727.discord.survplanet.entity.Player;
import com.remgagagali727.discord.survplanet.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class PlayerController {

    @Autowired
    private  PlayerRepository playerRepository;
    @Autowired
    private DrillRepository drillRepository;
    @Autowired
    private RodRepository rodRepository;
    @Autowired
    private WeaponRepository weaponRepository;
    @Autowired
    private SpaceshipRepository spaceshipRepository;

    public Player getPlayer(long idLong) {
        Optional<Player> optionalPlayer = playerRepository.findById(idLong);
        return optionalPlayer.orElseGet(() ->{
                    Player newPlayer = new Player(idLong);
                    newPlayer.setCoins("0");
                    newPlayer.setHealth("100");
                    newPlayer.setDrill(drillRepository.getReferenceById(0L));
                    newPlayer.setRod(rodRepository.getReferenceById(1L));
                    newPlayer.setWeapon(weaponRepository.getReferenceById(2L));
                    newPlayer.setSpaceship(spaceshipRepository.getReferenceById(3L));
                    newPlayer.setN_fish(LocalDateTime.now());
                    newPlayer.setN_mine(LocalDateTime.now());
                    newPlayer.setN_hunt(LocalDateTime.now());
                    return playerRepository.save(newPlayer);
                });
    }
}
