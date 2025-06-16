package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Planet;
import com.remgagagali727.discord.survplanet.repository.PlanetRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.util.List;

@Controller
public class PlanetController{

    @Autowired
    private PlanetRepository planetRepository;

    private static final int HUNT = 0;
    private static final int MINE = 1;
    private static final int FISH = 2;

    public void mine(MessageReceivedEvent event) {
        EmbedBuilder message = new EmbedBuilder();
        if(inCooldown()) {
            message.setTitle("Oh no, your drill is currently in cooldown...");
            message.addField("Cooldown", "you can mine at %time% try later :p", false);
        } else {
            message.setColor(Color.YELLOW);
            message.setAuthor(event.getAuthor().getEffectiveName() + " is mining at planet " + "%planet%");
            message.setImage("https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExNWttOTZ6dHNhZjQycXI3ZzR5ZzBndDV5bWdiZW1rZXJjNGNvYng3aSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/KbCaO3y2yH5qo/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
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
        for(int i = (int) page * 10;i < Long.min((page + 1) * 10, planets.size());i++) {
            Planet item = planets.get(i);
            String items = "(" + item.getId() + ") " + item.getName() + "\n";
            mes.append(items);
        }
        mes.append("Page ").append(page + 1);
        event.getChannel().sendMessage(mes.toString()).queue();
    }
}
