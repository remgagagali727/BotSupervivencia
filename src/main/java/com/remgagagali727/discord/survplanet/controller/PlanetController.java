package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.*;
import com.remgagagali727.discord.survplanet.repository.ItemRelationRepository;
import com.remgagagali727.discord.survplanet.repository.LootRepository;
import com.remgagagali727.discord.survplanet.repository.PlanetRepository;
import com.remgagagali727.discord.survplanet.repository.TypeRepository;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.List;

@Controller
public class PlanetController{

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private LootRepository lootRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private PlayerController playerController;

    @Autowired
    private ItemRelationRepository itemre;

    private static final long HUNT = 0;
    private static final long FISH = 1;
    private static final long MINE = 2;

    @Transactional
    public void mine(MessageReceivedEvent event) {
        EmbedBuilder message = new EmbedBuilder();
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        if(!player.isOnPlanet()) {
            player.notInPlanet(event);
            return;
        }
        if(minCooldown(player)) {
            message.setTitle("Oh no, your drill is currently in cooldown...");

            LocalDateTime localDateTime = player.getN_mine();
            long timeStamp = localDateTime.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
            String tiempo = "<t:" + timeStamp + ":R>";

            message.addField("Cooldown", "you can mine " + tiempo + "  try later :p", false);
        } else {
            Planet planet = player.getPlanet();
            BigInteger extraMinutes = new BigInteger(planet.getToughness()).divide(new BigInteger(player.getDrill().getToughness()));
            int got = (int)(Math.random() * 3 + 1);
            BigInteger bgot = new BigInteger(String.valueOf(got));
            if(extraMinutes.compareTo(new BigInteger("120")) > 0) {
                longAction("mine", event);
                return;
            }
            BigInteger damage = bgot.multiply(extraMinutes);
            if(damage.compareTo(new BigInteger(player.getHealth())) >= 0) {
                playerController.kill(event);
                return;
            }
            BigInteger health = new BigInteger(player.getHealth());
            health = health.add(damage.negate());
            BigInteger extraCoins = new BigInteger(planet.getToughness()).multiply(new BigInteger(player.getDrill().getToughness()));
            LocalDateTime newMine = LocalDateTime.now().plusMinutes(extraMinutes.intValueExact());
            List<Loot> loots = lootRepository.findByPlanetAndType(planet, typeRepository.getReferenceById(MINE));
            message.addField("Coins :coin:", extraCoins.toString(), true);
            for(Loot loot : loots) {
                got = (int)(Math.random() * 3 + 1);
                bgot = new BigInteger(String.valueOf(got));
                Item item = loot.getItem();
                BigInteger newAmount = bgot.multiply(new BigInteger(loot.getAmount())).multiply(new BigInteger(planet.getToughness()));
                message.addField(item.getName(), newAmount.toString(), true);
                playerController.addToInventory(item, newAmount.toString(), event);
            }
            message.addField("Lost hearts :broken_heart:", damage.toString(), true);
            player.setN_mine(newMine);
            player.setHealth(health.toString());
            player.setCoins(extraCoins.add(new BigInteger(player.getCoins())).toString());
            playerController.savePlayer(player);
            message.setColor(Color.YELLOW);
            message.setAuthor(event.getAuthor().getEffectiveName() + " just mined at planet " + player.getPlanet().getName() + " and got...");
            message.setImage("https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExNWttOTZ6dHNhZjQycXI3ZzR5ZzBndDV5bWdiZW1rZXJjNGNvYng3aSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/KbCaO3y2yH5qo/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }

    private void longAction(String text, MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Oh no!!!")
                .setDescription("To " + text + " here you would need more than 200 minutes you can't do that!!!")
                .setFooter("Survival Universe Bot")
                .setColor(Color.yellow);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private boolean minCooldown(Player player) {
        return player.getN_mine().isAfter(LocalDateTime.now());
    }

    public void fish(MessageReceivedEvent event) {
        EmbedBuilder message = new EmbedBuilder();
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        if(!player.isOnPlanet()) {
            LocalDateTime localDateTime = player.getN_mine();
            long timeStamp = localDateTime.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
            String tiempo = "<t:" + timeStamp + ":R>";
            message.setTitle("You are not in a planet, you will arive to a planet " + tiempo);
            message.setColor(Color.BLACK);
        } else if(fishCooldown(player)) {
            message.setTitle("Oh no, your fishing rod is currently in cooldown...");

            LocalDateTime localDateTime = player.getN_mine();
            long timeStamp = localDateTime.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
            String tiempo = "<t:" + timeStamp + ":R>";

            message.addField("Cooldown", "you can fish <t:" + timeStamp + ":R> try later :p", false);
        } else {
            Planet planet = player.getPlanet();
            BigInteger extraMinutes = new BigInteger(planet.getToughness()).divide(new BigInteger(player.getRod().getToughness()));
            int got = (int)(Math.random() * 3 + 1);
            BigInteger bgot = new BigInteger(String.valueOf(got));
            if(extraMinutes.compareTo(new BigInteger("120")) > 0) {
                event.getChannel().sendMessage("You would need " + extraMinutes + " minutes to fish here but thats to much, you can't fish here").queue();
                return;
            }
            BigInteger damage = bgot.multiply(extraMinutes);
            if(damage.compareTo(new BigInteger(player.getHealth())) >= 0) {
                playerController.kill(event);
                return;
            }
            BigInteger health = new BigInteger(player.getHealth());
            health = health.add(damage.negate());
            player.setHealth(health.toString());
            BigInteger extraCoins = new BigInteger(planet.getToughness()).multiply(new BigInteger(player.getRod().getToughness()));
            LocalDateTime newMine = LocalDateTime.now().plusMinutes(extraMinutes.intValueExact());
            List<Loot> loots = lootRepository.findByPlanetAndType(planet, typeRepository.getReferenceById(FISH));
            message.addField("Coins :coin:", extraCoins.toString(), true);
            for(Loot loot : loots) {
                got = (int)(Math.random() * 3 + 1);
                bgot = new BigInteger(String.valueOf(got));
                Item item = loot.getItem();
                Optional<ItemRelation> itemRelationOptional = itemre.findByPlayerAndItem(player, item);
                ItemRelation nir = new ItemRelation();
                BigInteger newAmount = bgot.multiply(new BigInteger(loot.getAmount())).multiply(new BigInteger(planet.getToughness()));
                message.addField(item.getName(), newAmount.toString(), true);
                if(itemRelationOptional.isEmpty()) {
                    nir.setItem(item);
                    nir.setPlayer(player);
                    nir.setId(new ItemRelation.ItemRelationId(player.getId(), item.getId()));
                    nir.setAmount(newAmount.toString());
                } else {
                    nir = itemRelationOptional.get();
                    nir.setItem(item);
                    nir.setPlayer(player);
                    nir.setId(new ItemRelation.ItemRelationId(player.getId(), item.getId()));
                    newAmount = newAmount.add(new BigInteger(nir.getAmount()));
                    nir.setAmount(newAmount.toString());
                }
                itemre.save(nir);
            }
            message.addField("Lost hearts :broken_heart:", damage.toString(), true);
            player.setN_mine(newMine);
            player.setCoins(extraCoins.add(new BigInteger(player.getCoins())).toString());
            playerController.savePlayer(player);
            message.setColor(Color.CYAN);
            message.setAuthor(event.getAuthor().getEffectiveName() + " just fished at planet " + player.getPlanet().getName() + " and got...");
            message.setImage("https://gifdb.com/images/high/under-water-sea-monster-mosasaurus-j5yo553nbgqb5crp.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }
    private boolean fishCooldown(Player player) {
        return player.getN_fish().isAfter(LocalDateTime.now());
    }

    public void hunt(MessageReceivedEvent event) {
        EmbedBuilder message = new EmbedBuilder();
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        if(!player.isOnPlanet()) {
            LocalDateTime localDateTime = player.getN_mine();
            long timeStamp = localDateTime.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
            String tiempo = "<t:" + timeStamp + ":R>";
            message.setTitle("You are not in a planet, you will arive to a planet " + tiempo);
            message.setColor(Color.BLACK);
        } else if(huntCooldown(player)) {
            message.setTitle("Oh no, your weapon is currently in cooldown...");

            LocalDateTime localDateTime = player.getN_mine();
            long timeStamp = localDateTime.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
            String tiempo = "<t:" + timeStamp + ":R>";

            message.addField("Cooldown", "you can hunt <t:" + timeStamp + ":R> try later :p", false);
        } else {
            Planet planet = player.getPlanet();
            BigInteger extraMinutes = new BigInteger(planet.getToughness()).divide(new BigInteger(player.getWeapon().getToughness()));
            int got = (int)(Math.random() * 3 + 1);
            BigInteger bgot = new BigInteger(String.valueOf(got));
            if(extraMinutes.compareTo(new BigInteger("120")) > 0) {
                event.getChannel().sendMessage("You would need " + extraMinutes + " minutes to hunt here but thats to much, you can't hunt here").queue();
                return;
            }
            BigInteger damage = bgot.multiply(extraMinutes);
            if(damage.compareTo(new BigInteger(player.getHealth())) >= 0) {
                playerController.kill(event);
                return;
            }
            BigInteger health = new BigInteger(player.getHealth());
            health = health.add(damage.negate());
            player.setHealth(health.toString());
            BigInteger extraCoins = new BigInteger(planet.getToughness()).multiply(new BigInteger(player.getWeapon().getToughness()));
            LocalDateTime newMine = LocalDateTime.now().plusMinutes(extraMinutes.intValueExact());
            List<Loot> loots = lootRepository.findByPlanetAndType(planet, typeRepository.getReferenceById(HUNT));
            message.addField("Coins :coin:", extraCoins.toString(), true);
            for(Loot loot : loots) {
                got = (int)(Math.random() * 3 + 1);
                bgot = new BigInteger(String.valueOf(got));
                Item item = loot.getItem();
                Optional<ItemRelation> itemRelationOptional = itemre.findByPlayerAndItem(player, item);
                ItemRelation nir = new ItemRelation();
                BigInteger newAmount = bgot.multiply(new BigInteger(loot.getAmount())).multiply(new BigInteger(planet.getToughness()));
                message.addField(item.getName(), newAmount.toString(), true);
                if(itemRelationOptional.isEmpty()) {
                    nir.setItem(item);
                    nir.setPlayer(player);
                    nir.setId(new ItemRelation.ItemRelationId(player.getId(), item.getId()));
                    nir.setAmount(newAmount.toString());
                } else {
                    nir = itemRelationOptional.get();
                    nir.setItem(item);
                    nir.setPlayer(player);
                    nir.setId(new ItemRelation.ItemRelationId(player.getId(), item.getId()));
                    newAmount = newAmount.add(new BigInteger(nir.getAmount()));
                    nir.setAmount(newAmount.toString());
                }
                itemre.save(nir);
            }
            message.addField("Lost hearts :broken_heart:", damage.toString(), true);
            player.setN_mine(newMine);
            player.setCoins(extraCoins.add(new BigInteger(player.getCoins())).toString());
            playerController.savePlayer(player);
            message.setColor(Color.RED);
            message.setAuthor(event.getAuthor().getEffectiveName() + " just hunted at planet " + player.getPlanet().getName() + " and got...");
            message.setImage("https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExdDBjaGg4NWRyZjc1aXQ2eDl0bDRzaHoyeWFkc240dXYzN3A2NWtubCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/FDFi1PMVdrMbivlfhf/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }
    private boolean huntCooldown(Player player) {
        return player.getN_fish().isAfter(LocalDateTime.now());
    }

    private boolean inCooldown() {
        return Math.random() * 2 < 1;
    }

    public void addPlanet(String command, MessageReceivedEvent event) {
        String[] s = command.split(", ");
        Planet planet = new Planet();
        planet.setName(s[0]);
        new BigInteger(s[1]);
        new BigInteger(s[2]);
        new BigInteger(s[3]);
        planet.setX(s[1]);
        planet.setY(s[2]);
        planet.setToughness(s[3]);
        planetRepository.save(planet);
        event.getChannel().sendMessage("Si se puedo xd").queue();
    }

    public void planets(String command, MessageReceivedEvent event) {
        if(command.startsWith("planets ")) command = command.substring(8);
        long page;
        try {
            page = Long.parseLong(command);
        } catch (Exception ignored) {
            UniverseController.invalidCommand(event);
            return;
        }
        List<Planet> planets = planetRepository.findAll();
        page = Long.min(page - 1, (planets.size() - 1) / 8);
        StringBuilder mes = new StringBuilder("**Planets**\n");
        planets.sort(Comparator.comparing(p -> new BigInteger(p.getToughness())));
        for(int i = (int) page * 8;i < Long.min((page + 1) * 8, planets.size());i++) {
            Planet item = planets.get(i);
            String items = "`[" + item.getId() + "]` **" + item.getName() + "**\n\tDifficulty: **" + item.getToughness() + "**\n";
            mes.append(items);
        }
        mes.append("Page ").append(page + 1).append(" / ").append((planets.size() + 7) / 8);
        event.getChannel().sendMessage(mes.toString()).queue();
    }
}
