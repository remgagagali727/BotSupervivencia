package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.entity.ItemRelation;
import com.remgagagali727.discord.survplanet.entity.Player;
import com.remgagagali727.discord.survplanet.repository.*;
import jakarta.transaction.Transactional;
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

    @Transactional
    public Player getPlayer(long idLong) {
        Optional<Player> optionalPlayer = playerRepository.findById(idLong);
        return optionalPlayer.orElseGet(() ->{
                    Player newPlayer = new Player(idLong);
                    newPlayer.setCoins("1");
                    newPlayer.setHealth("100");
                    newPlayer.setMaxHealth("100");
                    newPlayer.setDrill(drillRepository.getReferenceById(1L));
                    newPlayer.setRod(rodRepository.getReferenceById(2L));
                    newPlayer.setWeapon(weaponRepository.getReferenceById(3L));
                    newPlayer.setSpaceship(spaceshipRepository.getReferenceById(4L));
                    newPlayer.setN_fish(LocalDateTime.now());
                    newPlayer.setN_mine(LocalDateTime.now());
                    newPlayer.setN_hunt(LocalDateTime.now());
                    newPlayer.setArrive(LocalDateTime.now());
                    newPlayer.setPlanet(planetRepository.getReferenceById(2L));
                    List<ItemRelation> inventory = new java.util.ArrayList<>(List.of());
                    for(long i = 1;i <= 4;i++) {
                        ItemRelation itemRelation = new ItemRelation();
                        itemRelation.setPlayer(newPlayer);
                        Item item = itemRepository.findById(i).get();
                        itemRelation.setItem(item);
                        itemRelation.setAmount("1");
                        itemRelation.setId(new ItemRelation.ItemRelationId(newPlayer.getId(), item.getId()));
                        inventory.add(itemRelation);
                    }
                    newPlayer.setInventory(inventory);
                    return playerRepository.save(newPlayer);
                });
    }

    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    // no ma escribe bien inventario
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
        List<ItemRelation> inventory = player.getInventory();
        page = Long.min(page - 1, (inventory.size() - 1) / 10);
        StringBuilder mes = new StringBuilder("**Inventory of " + event.getAuthor().getEffectiveName() + "**\n");
        for(int i = (int) page * 10;i < Long.min((page + 1) * 10, inventory.size());i++) {
            Item item = inventory.get(Math.toIntExact(i)).getItem();
            String items = "(" + item.getId() + ") " + item.getName() + " -> " + item.getDescription() + " " + inventory.get(i).getAmount() + "\n";
            mes.append(items);
        }
        mes.append("Page ").append(page + 1);
        event.getChannel().sendMessage(mes.toString()).queue();
    }
}
