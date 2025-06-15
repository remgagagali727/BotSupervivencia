package com.remgagagali727.discord.survplanet.controllers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ProfileController {

    public final MessageReceivedEvent event;

    public ProfileController(MessageReceivedEvent event) {
        this.event = event;
    }

    public void profile() {
        event.getChannel().sendMessage("Intentado checar un perfil").queue();
        EmbedBuilder eBuilder = new EmbedBuilder()
                .setAuthor("Profile of " + event.getAuthor().getGlobalName(), null, event.getAuthor().getAvatarUrl())
                .setImage("https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExdWZ1ZnZqbHRuNzJ0bnU5c2VqZWhpODN4czVwdTJwdnQ4dDhtd3VkZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/SVCSsoKU5v6ZJLk07n/giphy.gif")
                .addField("Campo", "texto", true)
                .addField("Cambo2", "texto2", true);
        event.getChannel().sendMessageEmbeds(eBuilder.build()).queue();
    }
}
