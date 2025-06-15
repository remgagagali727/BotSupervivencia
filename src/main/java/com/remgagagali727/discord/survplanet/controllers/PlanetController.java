package com.remgagagali727.discord.survplanet.controllers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlanetController {

    private final MessageReceivedEvent event;
    private final int HUNT = 0;
    private final int MINE = 1;
    private final int FISH = 2;

    public PlanetController(MessageReceivedEvent event) {
        this.event = event;
    }

    public void mine() {
        EmbedBuilder message = new EmbedBuilder();
        if(inCooldown()) {
            message.setTitle("Oh no, your drill is currently in cooldown...");
            message.addField("Cooldown", "you can mine at %time% try later :p", false);
        } else {
            message.setAuthor(event.getAuthor().getEffectiveName() + " is mining at planet " + "%planet%");
            message.setImage("https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExNWttOTZ6dHNhZjQycXI3ZzR5ZzBndDV5bWdiZW1rZXJjNGNvYng3aSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/KbCaO3y2yH5qo/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }

    public void fish() {
        EmbedBuilder message = new EmbedBuilder();
        if(inCooldown()) {
            message.setTitle("Oh no, your fishing rod is currently in cooldown...");
            message.addField("Cooldown", "you can fish at %time% try later :p", false);
        } else {
            message.setAuthor(event.getAuthor().getEffectiveName() + " is fishing at planet " + "%planet%");
            message.setImage("https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExbHFwemt2cTh5M2swcXFheTNpZnA2Z3RpM2Y5N3NsNHRvZThkZnZzaSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/SiEJuFiOrNHeRjYmUy/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }

    public void hunt() {
        EmbedBuilder message = new EmbedBuilder();
        if(inCooldown()) {
            message.setTitle("Oh no, your weapon rod is currently in cooldown...");
            message.addField("Cooldown", "you can hunt at %time% try later :p", false);
        } else {
            message.setAuthor(event.getAuthor().getEffectiveName() + " is hunting at planet " + "%planet%");
            message.setImage("https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExcWVsZXB4b3F6Y2tsajMxdGJjcWhvYWxpeHFvMzN4enN4ejE1eDJ3OCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/BDSZj7aLlvE7MXa90V/giphy.gif");
        }
        event.getChannel().sendMessageEmbeds(message.build()).queue();
    }

    private boolean inCooldown() {
        return Math.random() * 2 < 1;
    }
}
