package com.remgagagali727.discord.survplanet.listener;

import com.remgagagali727.discord.survplanet.controller.BotController;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        new BotController(event);
    }
}
