package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.*;
import com.remgagagali727.discord.survplanet.repository.ItemRelationRepository;
import com.remgagagali727.discord.survplanet.repository.LootRepository;
import com.remgagagali727.discord.survplanet.repository.PlanetRepository;
import com.remgagagali727.discord.survplanet.repository.TypeRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
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

    public void mine(MessageReceivedEvent event) {
        EmbedBuilder message = new EmbedBuilder();
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        if(!player.isOnPlanet()) {
            message.setTitle("You are not in a planet, wait until you get to one in order to mine :D");
            message.setColor(Color.BLACK);
        } else if(minCooldown(player)) {
            message.setTitle("Oh no, your drill is currently in cooldown...");
            message.addField("Cooldown", "you can mine at " + player.getN_mine() + " try later :p", false);
        } else {
            Planet planet = player.getPlanet();
            BigInteger extraMinutes = new BigInteger(planet.getToughness()).divide(new BigInteger(player.getDrill().getToughness()));
            int got = (int)(Math.random() * 3 + 1);
            BigInteger bgot = new BigInteger(String.valueOf(got));
            if(extraMinutes.compareTo(new BigInteger("120")) > 0) {
                event.getChannel().sendMessage("You would need " + extraMinutes + " minutes to mine here but thats to much, you can't mine here").queue();
                return;
            }
            BigInteger damage = bgot.multiply(extraMinutes);
            if(damage.compareTo(new BigInteger(player.getHealth())) > 0) {
                event.getChannel().sendMessage("You would get killed if you mined here, thank the god of this planet for saving you").queue();
                return;
            }
            BigInteger health = new BigInteger(player.getHealth());
            health = health.add(damage.negate());
            player.setHealth(health.toString());
            BigInteger extraCoins = new BigInteger(planet.getToughness()).multiply(new BigInteger(player.getDrill().getToughness()));
            LocalDateTime newMine = LocalDateTime.now().plusMinutes(extraMinutes.intValueExact());
            List<Loot> loots = lootRepository.findByPlanetAndType(planet, typeRepository.getReferenceById(MINE));
            StringBuilder msg = new StringBuilder("**You got**\n");
            msg.append(extraCoins).append(" coins!!!\n");
            for(Loot loot : loots) {
                got = (int)(Math.random() * 3 + 1);
                Item item = loot.getItem();
                Optional<ItemRelation> itemRelationOptional = itemre.findByPlayerAndItem(player, item);
                ItemRelation nir = new ItemRelation();
                if(itemRelationOptional.isEmpty()) {
                    nir.setItem(item);
                    nir.setPlayer(player);
                    nir.setAmount(String.valueOf(got));
                } else {
                    nir = itemRelationOptional.get();
                    nir.setAmount(new BigInteger(nir.getAmount()).add(new BigInteger(String.valueOf(got))).toString());
                }
                itemre.save(nir);
                msg.append(got).append(" ").append(item.getName()).append('\n');
            }
            msg.append("And you lost ").append(damage).append(" hearts");
            player.setN_mine(newMine);
            player.setCoins(extraCoins.add(new BigInteger(player.getCoins())).toString());
            playerController.savePlayer(player);
            message.setColor(Color.YELLOW);
            message.setAuthor(event.getAuthor().getEffectiveName() + " is mining at planet " + player.getPlanet().getName());
            message.setImage("https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExNWttOTZ6dHNhZjQycXI3ZzR5ZzBndDV5bWdiZW1rZXJjNGNvYng3aSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/KbCaO3y2yH5qo/giphy.gif");
            event.getChannel().sendMessage(msg.toString()).queue();
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }

    private boolean minCooldown(Player player) {
        return player.getN_mine().isAfter(LocalDateTime.now());
    }

    public void fish(MessageReceivedEvent event) {
        EmbedBuilder message = new EmbedBuilder();
        if(inCooldown()) {
            message.setTitle("Oh no, your fishing rod is currently in cooldown...");
            message.addField("Cooldown", "you can fish at %time% try later :p", false);
        } else {
            message.setColor(Color.CYAN);
            message.setAuthor(event.getAuthor().getEffectiveName() + " is fishing at planet " + "%planet%");
            message.setImage("https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExbHFwemt2cTh5M2swcXFheTNpZnA2Z3RpM2Y5N3NsNHRvZThkZnZzaSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/SiEJuFiOrNHeRjYmUy/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }

    public void hunt(MessageReceivedEvent event) {
        EmbedBuilder message = new EmbedBuilder();
        if(inCooldown()) {
            message.setTitle("Oh no, your weapon is currently in cooldown...");
            message.addField("Cooldown", "you can hunt at %time% try later :p", false);
        } else {
            message.setColor(Color.BLUE);
            message.setAuthor(event.getAuthor().getEffectiveName() + " is hunting at planet " + "%planet%");
            message.setImage("https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExcWVsZXB4b3F6Y2tsajMxdGJjcWhvYWxpeHFvMzN4enN4ejE1eDJ3OCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/BDSZj7aLlvE7MXa90V/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }

    private boolean inCooldown() {
        return Math.random() * 2 < 1;
    }

    public void addPlanet(String command, MessageReceivedEvent event) {
        String[] s = command.split(" ");
        Planet planet = new Planet();
        planet.setName(s[0]);
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
        page = Long.min(page - 1, (planets.size() - 1) / 10);
        StringBuilder mes = new StringBuilder("**Planets**\n");
        planets.sort(Comparator.comparing(p -> new BigInteger(p.getToughness())));
        for(int i = (int) page * 10;i < Long.min((page + 1) * 10, planets.size());i++) {
            Planet item = planets.get(i);
            String items = "(" + item.getId() + ") " + item.getName() + " " + item.getToughness() + "\n";
            mes.append(items);
        }
        mes.append("Page ").append(page + 1);
        event.getChannel().sendMessage(mes.toString()).queue();
    }
}
