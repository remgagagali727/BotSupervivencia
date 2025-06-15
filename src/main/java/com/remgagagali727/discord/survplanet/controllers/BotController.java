package com.remgagagali727.discord.survplanet.controllers;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotController {

    private final MessageReceivedEvent event;
    private final ProfileController profileController;
    private final PlanetController planetController;
    private final String command;

    public BotController(MessageReceivedEvent event) {
        this.event = event;
        this.profileController = new ProfileController(event);
        this.planetController = new PlanetController(event);
        String command;
        try {
            command = event.getMessage().getContentRaw().substring(2);
        } catch (Exception e) {
            command = "";
        }
        this.command = command;
        if(notValid()) return;
        doCommand();
    }

    private void doCommand() {
        switch (command) {
            case "profile":
            case "p":
                profileController.profile();
                break;
            case "m":
            case "mine":
                planetController.mine();
                break;
            case "f":
            case "fish":
                planetController.fish();
                break;
            case "hunt":
                planetController.hunt();
                break;
        }
    }

    private boolean notValid() {
        return event.getAuthor().isBot() || !event.getMessage().getContentRaw().startsWith("s!");
    }
}
