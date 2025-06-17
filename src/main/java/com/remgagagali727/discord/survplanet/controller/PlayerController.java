package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.entity.ItemRelation;
import com.remgagagali727.discord.survplanet.entity.Player;
import com.remgagagali727.discord.survplanet.repository.*;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    @Autowired
    private ItemRelationRepository itemRelationRepository;

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

    @Transactional
    public void savePlayer(Player player) {
        playerRepository.save(player);
    }

    public void inventory(String command, MessageReceivedEvent event) {
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

    @Transactional
    public void equip(String command, MessageReceivedEvent event) {
        Player player = getPlayer(event.getAuthor().getIdLong());
        Long iid = Long.parseLong(command);
        StringBuilder sb = new StringBuilder();

        Optional<Item> optionalItem = itemRepository.findById(iid);
        if(optionalItem.isEmpty()) {
            event.getChannel().sendMessage("Item with id " + iid + " does not exist").queue();
            return;
        }
        Item item = optionalItem.get();
        Optional<ItemRelation> itemRelation = itemRelationRepository.findByPlayerAndItem(player, item);

        if(itemRelation.isEmpty()) {
            event.getChannel().sendMessage("You don't own a " + item.getName()).queue();
            return;
        }

        Optional<Drill> optionalDrill = drillRepository.findById(iid);
        if (optionalDrill.isPresent()) {
            player.setDrill(optionalDrill.get());
            sb.append("You successfully equipped a ").append(item.getName()).append(" as a drill \n");
        }

        Optional<Rod> optionalRod = rodRepository.findById(iid);
        if (optionalRod.isPresent()) {
            sb.append("You successfully equipped a ").append(item.getName()).append(" as a rod \n");
            player.setRod(optionalRod.get());
        }

        Optional<Weapon> optionalWeapon = weaponRepository.findById(iid);
        if (optionalWeapon.isPresent()) {
            sb.append("You successfully equipped a ").append(item.getName()).append(" as a weapon \n");
            player.setWeapon(optionalWeapon.get());
        }

        Optional<Spaceship> optionalSpaceship = spaceshipRepository.findById(iid);
        if (optionalSpaceship.isPresent()) {
            sb.append("You successfully equipped a ").append(item.getName()).append(" as a spaceship \n");
            player.setSpaceship(optionalSpaceship.get());
        }
        event.getChannel().sendMessage(sb.toString()).queue();
        playerRepository.save(player);
    }

    @Transactional
    public void kill(MessageReceivedEvent event) {
        Player player = getPlayer(event.getAuthor().getIdLong());
        List<Player> players = playerRepository.findAll();
        Player coinPlayer = players.get((int) (Math.random() * players.size()));
        player.setHealth(player.getMaxHealth());
        player.setCoins("0");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("Oh no... what a shame " + event.getAuthor().getEffectiveName() + " just died");
        eb.setDescription("The gods took the death of " + event.getAuthor().getEffectiveName() + " as a tribute and decided to give one coin to <@" + coinPlayer.getId() + ">");
        eb.setImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRSAYD3V5Jx5VpENf0RYKAGW49KzscES0tFIA&s");
        LocalDateTime muerte = LocalDateTime.now().plusHours(2);
        long timeStamp = muerte.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
        String tiempo = "<t:" + timeStamp + ":R>";
        eb.addField("You will be alive again...", tiempo, true);player.setN_mine(muerte);
        player.setN_fish(muerte);
        player.setArrive(muerte);
        player.setN_hunt(muerte);
        coinPlayer.setCoins(new BigInteger("1").add(new BigInteger(coinPlayer.getCoins())).toString());
        savePlayer(coinPlayer);
        savePlayer(player);
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
