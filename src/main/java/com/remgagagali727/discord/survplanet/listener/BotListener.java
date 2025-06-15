package com.remgagagali727.discord.survplanet.listener;

import com.remgagagali727.discord.survplanet.controller.BotController;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotListener extends ListenerAdapter {

    @Autowired
    private BotController botController;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        botController.handleEvent(event);
    }
}
