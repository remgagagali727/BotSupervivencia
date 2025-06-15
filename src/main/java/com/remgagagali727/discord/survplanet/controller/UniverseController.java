package com.remgagagali727.discord.survplanet.controller;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class UniverseController extends BasicController{

    private String casinoPlanet = "%nah%";
    private Integer playerCoins;

    public UniverseController(MessageReceivedEvent event) {
        super(event);
    }


    public void casino(String command) {
        if(command.startsWith("casino ")) command = command.substring(7);
        else if(command.startsWith("cas ")) command = command.substring(4);
        Integer betCoins = 0;
        playerCoins = getPlayerCoins();
        try {
            betCoins = Integer.valueOf(command);
            if(betCoins > playerCoins || betCoins < 0) {
                event.getChannel().sendMessage("The amount of coins is an invalid amount").queue();
                return;
            }
        } catch (Exception e) {
            invalidCommand();
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if(inCooldown()) {
            embedBuilder.setDescription("The casino is currently at planet " + casinoPlanet);
        } else {
            embedBuilder.setAuthor(event.getAuthor().getEffectiveName() + " just bet " + betCoins);
            Boolean won = probabilityCasino();
            if(won) {
                embedBuilder.setColor(Color.GREEN);
                embedBuilder.setDescription("And won " + (2 * betCoins) + " :D");
                embedBuilder.setImage("https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExbzN3aXF6azc2d3I1ZTJwMDN6bHhpMjI1bjVzaDhweXVqNHlmZWVwZiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/gULnb1XcI8iC3a8jAp/giphy.gif");
            } else {
                embedBuilder.setDescription("And lost all the money :(");
                embedBuilder.setColor(Color.RED);
            }
        }
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private Integer getPlayerCoins() {
        return 100;
    }

    private void invalidCommand() {
        event.getChannel().sendMessage("The command is invalid try reading s!help").queue();
    }

    private Boolean probabilityCasino() {
        return Math.random() * 100 < 48;
    }

    private boolean inCooldown() {
        return Math.random() * 2 < 1;
    }
}
