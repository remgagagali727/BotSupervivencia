package com.remgagagali727.discord.survplanet.controllers;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotController {

    private final MessageReceivedEvent event;
    private final ProfileController profileController;
    private final PlanetController planetController;
    private final UniverseController universeController;
    private final String command;

    public BotController(MessageReceivedEvent event) {
        this.event = event;
        this.profileController = new ProfileController(event);
        this.universeController = new UniverseController(event);
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
                return;
            case "m":
            case "mine":
                planetController.mine();
                return;
            case "f":
            case "fish":
                planetController.fish();
                return;
            case "hunt":
            case "h":
                planetController.hunt();
                return;
            case "help":
                help();
                return;
            case "cas":
            case "casino":
                casHelp();
                return;
        }
        if(command.startsWith("cas") || command.startsWith("casino")) universeController.casino(command);
    }

    private void casHelp() {
        String helpMessage = """
                In order to use the casino command you need to put an amount of coins just after the command
                ie.
                s!casino 100
                """;
        help(helpMessage);
    }

    private void help() {
        String helpMessage = """
                **Use s!(command) to execute a command**
                Commands available
                
                profile -> This command lets you know your profile information 
                p -> Same as profile
                mine -> This commands lets you mine in the planet you are currently on
                m -> Same as mine
                fish -> This commands lets you fish in the planet you are currently on
                f -> Same as fish
                hunt -> This commands lets you hunt in the planet you are currently on
                h -> Same as hunt 
                help -> This command shows this menu
                casino (number) -> This command lets you bet your coins if you are currently in the planet the casino is on, else this command will show you where the casino is
                cas (number) -> Same as casino
                """;
        help(helpMessage);
    }

    private void help(String message) {
        event.getChannel().sendMessage(message).queue();
    }

    private boolean notValid() {
        return event.getAuthor().isBot() || !event.getMessage().getContentRaw().startsWith("s!");
    }
}
