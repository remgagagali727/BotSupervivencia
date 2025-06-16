package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Planet;
import com.remgagagali727.discord.survplanet.entity.Player;
import com.remgagagali727.discord.survplanet.repository.PlanetRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class UniverseController{

    @Autowired
    private PlayerController playerController;
    @Autowired
    private PlanetRepository planetRepository;

    private String casinoPlanet = "%nah%";

    public void casino(String command, MessageReceivedEvent event) {
        if(command.startsWith("casino ")) command = command.substring(7);
        else if(command.startsWith("cas ")) command = command.substring(4);
        BigInteger betCoins;
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        BigInteger playerCoins = new BigInteger(player.getCoins());
        try {
            betCoins = new BigInteger(command);
            if(betCoins.compareTo(playerCoins) > 0 || betCoins.compareTo(new BigInteger("0")) <= 0) {
                event.getChannel().sendMessage("The amount of coins is an invalid amount").queue();
                return;
            }
        } catch (Exception e) {
            invalidCommand(event);
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if(inCooldown()) {
            embedBuilder.setDescription("The casino is currently at planet " + casinoPlanet);
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
                invalidCommand(event);
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
            event.getChannel().sendMessage("You are already in that planet :D").queue();
            return;
        }
        BigDecimal x1, x2, y1, y2;
        x1 = new BigDecimal(planet.getX());
        x2 = new BigDecimal(playerPlanet.getX());
        y1 = new BigDecimal(planet.getY());
        y2 = new BigDecimal(playerPlanet.getY());
        BigDecimal time = (x1.add(x2.negate()).abs().pow(2).add(y1.add(y2.negate()).abs().pow(2))).sqrt(new MathContext(10)).divide(new BigDecimal(player.getSpaceship().getSpeed()));
        if(time.compareTo(BigDecimal.valueOf(120)) > 0) {
            event.getChannel().sendMessage("The time is too long (more than 2 hours) you cant go there").queue();
            return;
        }
        double mtime = time.doubleValue();
        player.setArrive(LocalDateTime.now().plusMinutes((long) mtime));
        player.setPlanet(planet);
        playerController.savePlayer(player);
        event.getChannel().sendMessage("You will arrive to " + planet.getName() + " at " + LocalDateTime.now().plusMinutes((long) mtime)).queue();
        event.getChannel().sendMessage("In exactly " + (long)mtime + " minutes").queue();
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
