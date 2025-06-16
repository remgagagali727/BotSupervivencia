package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.entity.Player;
import com.remgagagali727.discord.survplanet.repository.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class PlayerController {

    @Autowired
    private ItemRepository itemRepository;
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
    @Autowired
    private PlanetRepository planetRepository;

    public Player getPlayer(long idLong) {
        Optional<Player> optionalPlayer = playerRepository.findById(idLong);
        return optionalPlayer.orElseGet(() ->{
                    Player newPlayer = new Player(idLong);
                    newPlayer.setCoins("0");
                    newPlayer.setHealth("100");
                    newPlayer.setDrill(drillRepository.getReferenceById(1L));
                    newPlayer.setRod(rodRepository.getReferenceById(2L));
                    newPlayer.setWeapon(weaponRepository.getReferenceById(3L));
                    newPlayer.setSpaceship(spaceshipRepository.getReferenceById(4L));
                    newPlayer.setN_fish(LocalDateTime.now());
                    newPlayer.setN_mine(LocalDateTime.now());
                    newPlayer.setN_hunt(LocalDateTime.now());
                    newPlayer.setArrive(LocalDateTime.now());
                    newPlayer.setPlanet(planetRepository.getReferenceById(2L));
                    newPlayer.setInventory(List.of(itemRepository.getReferenceById(4L),
                            itemRepository.getReferenceById(1L),
                            itemRepository.getReferenceById(2L),
                            itemRepository.getReferenceById(3L)));
                    return playerRepository.save(newPlayer);
                });
    }

    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    public void invetory(String command, MessageReceivedEvent event) {
        if(command.startsWith("i ")) command = command.substring(2);
        else if(command.startsWith("inventory ")) command = command.substring(10);
        long page;
        try {
            page = Long.parseLong(command);
        } catch (Exception ignored) {
            UniverseController.invalidCommand(event);
            return;
        }
        Player player = getPlayer(event.getAuthor().getIdLong());
        List<Item> inventory = player.getInventory();
        page = Long.min(page - 1, (inventory.size() - 1) / 10);
        StringBuilder mes = new StringBuilder("**Inventory of " + event.getAuthor().getEffectiveName() + "**\n");
        for(int i = (int) page * 10;i < Long.min((page + 1) * 10, inventory.size());i++) {
            Item item = inventory.get(Math.toIntExact(i));
            String items = "(" + item.getId() + ") " + item.getName() + " -> " + item.getDescription() + "\n";
            mes.append(items);
        }
        mes.append("Page ").append(page + 1);
        event.getChannel().sendMessage(mes.toString()).queue();
    }
}
