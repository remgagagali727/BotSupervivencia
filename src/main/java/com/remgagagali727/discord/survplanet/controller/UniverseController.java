package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.*;
import com.remgagagali727.discord.survplanet.repository.CraftingRepository;
import com.remgagagali727.discord.survplanet.repository.LocationRepository;
import com.remgagagali727.discord.survplanet.repository.PlanetRepository;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

@Controller
public class UniverseController{

    public static final long CASINO = 0;

    @Autowired
    private PlayerController playerController;
    @Autowired
    private PlanetRepository planetRepository;
    @Autowired
    private CraftingRepository craftingRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public void casino(String command, MessageReceivedEvent event) {
        if(command.startsWith("casino ")) command = command.substring(7);
        else if(command.startsWith("cas ")) command = command.substring(4);
        BigInteger betCoins;
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        if(!player.isOnPlanet()) {
            player.notInPlanet(event);
            return;
        }
        BigInteger playerCoins = new BigInteger(player.getCoins());
        try {
            betCoins = new BigInteger(command);
            if(betCoins.compareTo(playerCoins) > 0 || betCoins.compareTo(new BigInteger("0")) <= 0) {
                casinoNoValid(event);
                return;
            }
        } catch (Exception e) {
            casinoHelp(event);
            return;
        }
        Location casino = locationRepository.getReferenceById(CASINO);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Planet planet = player.getPlanet();
        if(!casinoInPlanet(planet, casino)) {
            embedBuilder.setTitle("The casino is currently at planet " + casino.getPlanet().getName());
            embedBuilder.setColor(Color.PINK);
        } else {
            embedBuilder.setAuthor(event.getAuthor().getEffectiveName() + " just bet " + betCoins);
            Boolean won = probabilityCasino();
            if(won) {
                playerCoins = playerCoins.add(betCoins);
                embedBuilder.setTitle(":slot_machine: Casino Result — WIN");
                embedBuilder.setColor(Color.GREEN);
                embedBuilder.setDescription("Congratulations, " + event.getAuthor().getEffectiveName() + "!\n" +
                        "You won the bet and earned +" + betCoins + " coins :coin:!\n\n" +
                        "Your new balance is: " + playerCoins + " coins :coin:.\n" +
                        "Feeling lucky? Try again!");
            } else {
                playerCoins = playerCoins.add(betCoins.negate());
                embedBuilder.setTitle(":slot_machine: Casino Result — LOSS");
                embedBuilder.setColor(Color.RED);
                embedBuilder.setDescription("Bad luck, " + event.getAuthor().getEffectiveName() + "!\n" +
                        "You won the bet and lost -" + betCoins + " coins :coin:!\n\n" +
                        "Your new balance is: " + playerCoins + " coins :coin:.\n" +
                        "Maybe next time luck will be on your side.");
            }
            player.setCoins(playerCoins.toString());
            playerController.savePlayer(player);
        }
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        if(moveCasino(casino, event)) {
            locationRepository.save(casino);
        }
    }

    private boolean casinoInPlanet(Planet planet, Location casino) {
        return planet.equals(casino.getPlanet());
    }

    @Transactional
    private boolean moveCasino(Location casino, MessageReceivedEvent event) {
        if(casino.getNext().isBefore(LocalDateTime.now())) {
            Planet newPlanet = planetRepository.findRandomPlanet().get();
            casino.setPlanet(newPlanet);
            casino.setNext(LocalDateTime.now().plusHours(2));

            LocalDateTime localDateTime = LocalDateTime.now().plusHours(2);
            long timeStamp = localDateTime.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
            String tiempo = "<t:" + timeStamp + ":R>";

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("The casino just moved location!!!");
            embedBuilder.setColor(Color.green);
            embedBuilder.setDescription("The new location of the casino is " + newPlanet.getName() + "\n" +
                    "The casino will move again " + tiempo);

            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

            return true;
        }
        return false;


    }

    private void casinoHelp(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Oh no, this command isn't valid try using the following format");
        embedBuilder.setDescription("s!casino 100");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void casinoNoValid(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("You can't bet this amount of coins because is invalid");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void go(String command, MessageReceivedEvent event) {
        if(command.startsWith("go ")) command = command.substring(3);
        Long id = -1L;
        try{
            id = Long.valueOf(command);
        } catch (Exception ignored) {

        }
        Optional<Planet> optionalPlanet = planetRepository.findById(id);
        Planet planet;
        if(optionalPlanet.isEmpty()) {
            optionalPlanet = planetRepository.findByNameIgnoreCase(command);
            if(optionalPlanet.isEmpty()) {
                invalidPlanet(event);
                return;
            } else {
                planet = optionalPlanet.get();
            }
        } else {
            planet = optionalPlanet.get();
        }
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        if(!player.isOnPlanet()) {
            player.notInPlanet(event);
            return;
        }
        Planet playerPlanet = player.getPlanet();
        if(planet.equals(playerPlanet)) {
            alreadyInPlanet(event);
            return;
        }
        BigDecimal x1, x2, y1, y2;
        x1 = new BigDecimal(planet.getX());
        x2 = new BigDecimal(playerPlanet.getX());
        y1 = new BigDecimal(planet.getY());
        y2 = new BigDecimal(playerPlanet.getY());
        BigDecimal time = (x1.add(x2.negate()).abs().pow(2).add(y1.add(y2.negate()).abs().pow(2))).sqrt(new MathContext(10)).divide(new BigDecimal(player.getSpaceship().getSpeed()));
        if(time.compareTo(BigDecimal.valueOf(120)) > 0) {
            longTravel(event);
            return;
        }
        double mtime = time.doubleValue();
        LocalDateTime arr = LocalDateTime.now().plusMinutes((long) mtime);
        player.setArrive(arr);
        player.setPlanet(planet);
        playerController.savePlayer(player);

        long timeStamp = arr.atZone(ZoneId.of("America/Mexico_City")).toEpochSecond();
        String tiempo = "<t:" + timeStamp + ":R>";

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("It's time to travel!!!")
                .setDescription("You will arrive to " + planet.getName() + " " + tiempo)
                .setFooter("Surv Planet")
                .setColor(Color.CYAN);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void longTravel(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Oh no!!!")
                .setDescription("This travel would take more than 2 hours and you don't have enough fuel, try traveling to another planet")
                .setFooter("Surv Planet")
                .setColor(Color.yellow);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void alreadyInPlanet(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("You are already in that planet")
                .setColor(Color.YELLOW)
                .setFooter("Surv Planet");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void invalidPlanet(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("That's not a planet!!! :ringed_planet:");
        embedBuilder.setColor(Color.MAGENTA);
        embedBuilder.setFooter("Surv Planet");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void invalidCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("The command is invalid try reading s!help").queue();
    }

    private Boolean probabilityCasino() {
        return Math.random() * 100 < 48;
    }

    public void crafts(String command, MessageReceivedEvent event) {
        long page;
        try {
            page = Long.parseLong(command);
        } catch (Exception ignored) {
            UniverseController.invalidCommand(event);
            return;
        }
        List<Crafting> craftings = craftingRepository.findAll();
        craftings.sort(Comparator.comparing(o -> o.getItem().getId()));
        ArrayList<ArrayList<Entry<Item, String>>> lists = new ArrayList<>();
        ArrayList<Entry<Item, String>> list = new ArrayList<>();
        list.add(new AbstractMap.SimpleEntry<>(craftings.getFirst().getItem(), craftings.getFirst().getAmount()));
        lists.add(list);
        Long curr = craftings.getFirst().getItem().getId();
        for(Crafting crafting : craftings) {
            System.out.println(crafting.getItem().getName() + " " + crafting.getRequired().getName() + " " + crafting.getAmount());
            if(((long) crafting.getItem().getId()) == (curr)) {
                list.add(new AbstractMap.SimpleEntry<>(crafting.getRequired(), crafting.getAmount()));
            } else {
                list = new ArrayList<>();
                list.add(new AbstractMap.SimpleEntry<>(crafting.getItem(), crafting.getAmount()));
                list.add(new AbstractMap.SimpleEntry<>(crafting.getRequired(), crafting.getAmount()));
                lists.add(list);
                curr = crafting.getItem().getId();
            }
        }
        page = Long.min(page - 1, (lists.size() - 1) / 2);
        StringBuilder mes = new StringBuilder("# **Crafteable Items**\n");
        for(int i = (int) page * 2;i < Long.min((page + 1) * 2, lists.size());i++) {
            List<Entry<Item, String>> items = lists.get(i);
            Item toCraft = items.getFirst().getKey();
            items.removeFirst();
            mes.append("\n`[").append(toCraft.getId()).append("]` **").append(toCraft.getName()).append("**\t");
            mes.append("Coins needed :coin: " + toCraft.getCrafting_price() + "\n\t");
            for(Entry<Item, String> entry : items) {
                toCraft = entry.getKey();
                String am = entry.getValue();
                mes.append("`[").append(toCraft.getId()).append("]` **").append(toCraft.getName()).append("**\n\t");
                mes.append("Amount: **").append(am).append("**\n\t");
            }
        }
        mes.append("\nPage ").append(page + 1).append(" / ").append(((lists.size() + 1) / 2));
        event.getChannel().sendMessage(mes.toString()).queue();
    }
}
