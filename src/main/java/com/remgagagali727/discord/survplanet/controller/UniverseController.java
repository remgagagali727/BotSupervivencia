package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Location;
import com.remgagagali727.discord.survplanet.entity.Planet;
import com.remgagagali727.discord.survplanet.entity.Player;
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
import java.util.Optional;

@Controller
public class UniverseController{

    public static final long CASINO = 0;

    @Autowired
    private PlayerController playerController;
    @Autowired
    private PlanetRepository planetRepository;
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
            playerCoins = playerCoins.add(betCoins.negate());
            if(won) {
                embedBuilder.setColor(Color.GREEN);
                betCoins = betCoins.multiply(new BigInteger("2"));
                embedBuilder.setDescription("And won " + betCoins + " :D");
                playerCoins = playerCoins.add(betCoins);
                embedBuilder.setImage("https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExbzN3aXF6azc2d3I1ZTJwMDN6bHhpMjI1bjVzaDhweXVqNHlmZWVwZiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/gULnb1XcI8iC3a8jAp/giphy.gif");
            } else {
                embedBuilder.setDescription("And lost all the money :(");
                embedBuilder.setColor(Color.RED);
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
            optionalPlanet = planetRepository.findByName(command);
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
                .setFooter("Survival Universe Bot")
                .setColor(Color.CYAN);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void longTravel(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Oh no!!!")
                .setDescription("This travel would take more than 2 hours and you don't have enough fuel, try traveling to another planet")
                .setFooter("Survival Universe Bot")
                .setColor(Color.yellow);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void alreadyInPlanet(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("You are already in that planet")
                .setColor(Color.YELLOW)
                .setFooter("Survival Universe Bot");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void invalidPlanet(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("That's not a planet!!! :ringed_planet:");
        embedBuilder.setColor(Color.MAGENTA);
        embedBuilder.setFooter("Survival Universe Bot");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public static void invalidCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("The command is invalid try reading s!help").queue();
    }

    private Boolean probabilityCasino() {
        return Math.random() * 100 < 48;
    }

    private boolean inCooldown() {
        return Math.random() * 2 < 1;
    }
}
