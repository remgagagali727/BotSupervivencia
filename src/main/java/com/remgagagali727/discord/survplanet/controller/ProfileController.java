package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileController{

    @Autowired
    private PlayerController playerController;

    public void profile(MessageReceivedEvent event) {
        Player player = playerController.getPlayer(event.getAuthor().getIdLong());
        EmbedBuilder eBuilder = new EmbedBuilder()
                .setAuthor("Profile of " + event.getAuthor().getGlobalName(), null, event.getAuthor().getAvatarUrl())
                .setImage("https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExdWZ1ZnZqbHRuNzJ0bnU5c2VqZWhpODN4czVwdTJwdnQ4dDhtd3VkZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/SVCSsoKU5v6ZJLk07n/giphy.gif")
                .addField("Coins :coin:", player.getCoins(), true)
                .addField("Equipped Drill", player.getDrill().getItem().getName(), true)
                .addField("Equipped Rod", player.getRod().getItem().getName(), true)
                .addField("Equipped Weapon", player.getWeapon().getItem().getName(), true)
                .addField("Equipped Spaceship", player.getSpaceship().getItem().getName(), true);

        if(player.isOnPlanet()) {
            eBuilder.addField("Location :ringed_planet:", player.getPlanet().getName(), true);
        } else {
            eBuilder.addField("Location :ringed_planet:", "in Space", true);
        }

        eBuilder.addField("Health", player.getHealth() + " :light_blue_heart: / " + player.getMaxHealth()  + " :light_blue_heart:", true);

        event.getChannel().sendMessageEmbeds(eBuilder.build()).queue();
    }
}
