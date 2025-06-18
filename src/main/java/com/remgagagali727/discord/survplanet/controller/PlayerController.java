package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.*;
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
    private CraftingRepository craftingRepository;
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
        page = Long.min(page - 1, (inventory.size() - 1) / 5);
        StringBuilder mes = new StringBuilder("# **Inventory of " + event.getAuthor().getEffectiveName() + "**\n");
        for(int i = (int) page * 5;i < Long.min((page + 1) * 5, inventory.size());i++) {
            Item item = inventory.get(Math.toIntExact(i)).getItem();
            String items = "`[" + item.getId() + "]` **" + item.getName() + "**\n\t**Description:** " + item.getDescription() + "\n\t**Quantity:** `" + inventory.get(i).getAmount() + "`\n";
            mes.append(items);
        }
        mes.append("Page ").append(page + 1).append(" / ").append((inventory.size() + 4) / 5);
        event.getChannel().sendMessage(mes.toString()).queue();
    }

    @Transactional
    public void equip(String command, MessageReceivedEvent event) {
        Player player = getPlayer(event.getAuthor().getIdLong());
        Item item;

        try {
            item = itemRepository.findByNameIgnoreCase(command).get();
        } catch (Exception e) {
            try {
                item = itemRepository.findById(Long.parseLong(command)).get();
            } catch (Exception ee) {
                itemDoesNotExist(event);
                return;
            }
        }

        Long iid = item.getId();

        StringBuilder sb = new StringBuilder();

        Optional<ItemRelation> itemRelation = itemRelationRepository.findByPlayerAndItem(player, item);

        if(itemRelation.isEmpty()) {
            event.getChannel().sendMessage("You don't own a " + item.getName()).queue();
            return;
        }

        Optional<Drill> optionalDrill = drillRepository.findById(iid);
        if (optionalDrill.isPresent()) {
            player.setDrill(optionalDrill.get());
            sb.append(item.getName()).append(" as a drill \n");
        }

        Optional<Rod> optionalRod = rodRepository.findById(iid);
        if (optionalRod.isPresent()) {
            sb.append(item.getName()).append(" as a rod \n");
            player.setRod(optionalRod.get());
        }

        Optional<Weapon> optionalWeapon = weaponRepository.findById(iid);
        if (optionalWeapon.isPresent()) {
            sb.append(item.getName()).append(" as a weapon \n");
            player.setWeapon(optionalWeapon.get());
        }

        Optional<Spaceship> optionalSpaceship = spaceshipRepository.findById(iid);
        if (optionalSpaceship.isPresent()) {
            sb.append(item.getName()).append(" as a spaceship \n");
            player.setSpaceship(optionalSpaceship.get());
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if(sb.isEmpty()) {
            embedBuilder.setTitle("Couldn't equip the item " + item.getName());
            embedBuilder.setColor(Color.BLUE);
        } else {
            embedBuilder.setTitle("Successfully equipped: ");
            embedBuilder.setColor(Color.PINK);
            embedBuilder.setDescription(sb.toString());
        }
        embedBuilder.setFooter("Survival Universal Bot");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        playerRepository.save(player);
    }

    private void itemDoesNotExist(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Oh no!!!")
                .setDescription("This is an invalid item, try using the ID or the Name of the item")
                .setFooter("Survival Universe Bot")
                .setColor(Color.RED);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
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

    @Transactional
    public void addToInventory(Item item, String number, MessageReceivedEvent event) {
        Player player = getPlayer(event.getAuthor().getIdLong());
        Optional<ItemRelation> oitemRelation = itemRelationRepository.findByPlayerAndItem(player, item);
        ItemRelation itemRelation;
        if(oitemRelation.isEmpty()) {
            itemRelation = new ItemRelation();
            itemRelation.setPlayer(player);
            itemRelation.setItem(item);
            itemRelation.setId(new ItemRelation.ItemRelationId(player.getId(), item.getId()));
            itemRelation.setAmount(number);
        } else {
            itemRelation = oitemRelation.get();
            itemRelation.setPlayer(player);
            itemRelation.setItem(item);
            itemRelation.setId(new ItemRelation.ItemRelationId(player.getId(), item.getId()));
            itemRelation.setAmount(new BigInteger(number).add
                    (new BigInteger(itemRelation.getAmount())).toString());
        }
        itemRelationRepository.save(itemRelation);
    }

    @Transactional
    public void craft(String command, MessageReceivedEvent event) {

        if (command.isEmpty()) {
            showHelpCraftEmbed(event);
            return;
        }

        Long userId = event.getAuthor().getIdLong();

        Player player = getPlayer(userId);

        Optional<Item> oitem = itemRepository.findByName(command);
        if(oitem.isEmpty()) {
            event.getChannel().sendMessage("The item does not exist").queue();
            return;
        }

        Item item = oitem.get();

        if(item.getCrafting_price().equals("-1")) {
            event.getChannel().sendMessage("This item cannot be crafted").queue();
            return;
        }

        if (!player.isOnPlanet()) {
            player.notInPlanet(event);
            return;
        }

        List<Crafting> craftings = craftingRepository.findByItem(item);
        for(Crafting craft : craftings) {
            if(!has(craft.getRequired(), craft.getAmount(), player)) {
                craftRecipe(craftings, item, event);
                return;
            }
        }
        BigInteger playerCoins = new BigInteger(player.getCoins());
        playerCoins = playerCoins.add(new BigInteger(item.getCrafting_price()).negate());

        if(playerCoins.compareTo(new BigInteger("0")) < 0) {
            craftRecipe(craftings, item, event);
            return;
        }

        for(Crafting craft : craftings) {
            addToInventory(craft.getRequired(), "-" + craft.getAmount(), event);
        }

        player.setCoins(playerCoins.toString());
        addToInventory(item, "1", event);

        savePlayer(player);

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(":tools:  Crafting Complete");
        embedBuilder.setDescription("You have successfully crafted: " + item.getName() + "\n\n" +
                item.getName() + " has been added to your inventory.");
        embedBuilder.setColor(Color.decode("#4CAF50"));
        embedBuilder.setImage("https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExMDdyNW5yYWh5eHBud3NxYWxzcDU2dGRzZzFoa2t4MDJ4cGYwMnFzZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/NpILbqtmLO1Qkfvc4f/giphy.gif");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void craftRecipe(List<Crafting> craftings, Item item, MessageReceivedEvent event) {
        Player player = getPlayer(event.getAuthor().getIdLong());
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("Oh no you can't craft this you will need")
                .addField("Coins :coin:", player.getCoins() + "\\" + item.getCrafting_price(), true)
                .setFooter("Space Survival Bot", null);

        for(Crafting crafting : craftings) {
            embedBuilder.addField(crafting.getRequired().getName(), has(crafting.getRequired(), player) + "\\" + crafting.getAmount(), true);
        }

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private String has(Item item, Player player) {
        Optional<ItemRelation> oItemRelation = itemRelationRepository.findByPlayerAndItem(player, item);
        if(oItemRelation.isEmpty()) return "0";
        else return oItemRelation.get().getAmount();
    }

    private boolean has(Item i, String amount, Player player) {
        Optional<ItemRelation> optional = itemRelationRepository.findByPlayerAndItem(player, i);
        if(optional.isEmpty()) return false;
        BigInteger am = new BigInteger(amount);
        BigInteger have = new BigInteger(optional.get().getAmount());
        return am.compareTo(have) <= 0;
    }

    private void showHelpCraftEmbed(MessageReceivedEvent event) {
        event.getChannel().sendMessage("To craft... bla bla bla").queue();
    }

    public void resetTimes(MessageReceivedEvent event) {
        Player player = getPlayer(event.getAuthor().getIdLong());
        LocalDateTime now = LocalDateTime.now();
        player.setN_hunt(now);
        player.setN_mine(now);
        player.setN_fish(now);
        player.setArrive(now);
        savePlayer(player);
        event.getChannel().sendMessage("Ya listo :p eres nuevo").queue();
    }

    public void heal(MessageReceivedEvent event) {
        Player player = getPlayer(event.getAuthor().getIdLong());
        player.setHealth(player.getMaxHealth());
        savePlayer(player);
        event.getChannel().sendMessage("Ya listo curado").queue();
    }
}
