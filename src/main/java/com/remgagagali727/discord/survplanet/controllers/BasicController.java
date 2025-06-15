package com.remgagagali727.discord.survplanet.controllers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BasicController {

    protected final MessageReceivedEvent event;
    public BasicController(MessageReceivedEvent event) {
        this.event = event;
    }

}
