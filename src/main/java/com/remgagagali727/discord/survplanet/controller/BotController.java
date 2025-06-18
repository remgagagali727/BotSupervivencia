package com.remgagagali727.discord.survplanet.controller;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BotController {
    @Autowired
    private ProfileController profileController;
    @Autowired
    private PlanetController planetController;
    @Autowired
    private UniverseController universeController;
    @Autowired
    private PlayerController playerController;
    @Autowired
    private FoodController foodController;
    @Autowired
    private ItemController itemController;

    private void doCommand(String command, MessageReceivedEvent event) {
        switch (command) {
            case "profile":
            case "p":
                profileController.profile(event);
                return;
            case "m":
            case "mine":
                planetController.mine(event);
                return;
            case "f":
            case "fish":
                planetController.fish(event);
                return;
            case "hunt":
            case "h":
                planetController.hunt(event);
                return;
            case "help":
                help(event);
                return;
            case "cas":
            case "casino":
                universeController.casino("cas", event);
                return;
            case "go":
                universeController.go("go", event);
                return;
            case "i":
            case "inventory":
                playerController.inventory("i 1", event);
                return;
            case "planets":
                planetController.planets("planets 1", event);
                return;
            case "items":
                itemController.items("1", event);
                return;
            case "eat":
                foodController.eat("", event);
                return;
            case "craft":
                playerController.craft("", event);
        }
        if(event.getChannel().getId().equals("1383991269654794341")) {
            if(command.startsWith("plan ")) planetController.addPlanet(command.substring(5), event);
            if(command.startsWith("item ")) itemController.addItem(command.substring(5), event);
            if(command.startsWith("drill ")) itemController.addDrill(command.substring(6), event);
            if(command.startsWith("rod ")) itemController.addRod(command.substring(4), event);
            if(command.startsWith("wea ")) itemController.addWeapon(command.substring(4), event);
            if(command.startsWith("spa ")) itemController.addSpaceship(command.substring(4), event);
            if(command.equals("getfood")) foodController.getFood(command, event);
            if(command.startsWith("food ")) foodController.addFood(command.substring(5), event);
            if(command.startsWith("recipe ")) itemController.addRecipe(command.substring(7), event);
            if(command.startsWith("cprice ")) itemController.setCraftingPrice(command.substring(7), event);
            if(command.startsWith("sprice ")) itemController.setSellPrice(command.substring(7), event);
            if(command.startsWith("loot ")) itemController.addLoot(command.substring(5), event);
            if(command.equals("reset")) playerController.resetTimes(event);
            if(command.equals("heal")) playerController.heal(event);
        }
        if(command.startsWith("cas ") || command.startsWith("casino ")) universeController.casino(command, event);
        if(command.startsWith("items ")) itemController.items(command.substring(6), event);
        if(command.startsWith("go ")) universeController.go(command, event);
        if(command.startsWith("i ") || command.startsWith("inventory ")) playerController.inventory(command, event);
        if(command.startsWith("planets ")) planetController.planets(command, event);
        if(command.startsWith("equip ")) playerController.equip(command.substring(6), event);
        if(command.startsWith("eat ")) foodController.eat(command, event);
        if(command.startsWith("craft ")) playerController.craft(command.substring(6), event);
    }

    private void goHelp(MessageReceivedEvent event) {
        String helpMessage = """
                In order to use the go you need to put the planet id or the planet name
                ie.
                s!go 1
                s!go earth
                """;
        help(helpMessage, event);
    }

    private void help(MessageReceivedEvent event) {
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
                go (planet) -> This command allows you to get to another planet if and only if you are in a planet that is no that planet
                i (page) -> This command allows you to see you inventory
                inventory (page) -> Same as i
                """;
        help(helpMessage, event);
    }

    private void help(String message, MessageReceivedEvent event) {
        event.getChannel().sendMessage(message).queue();
    }

    private boolean notValid(MessageReceivedEvent event) {
        return event.getAuthor().isBot() || !event.getMessage().getContentRaw().startsWith("s!");
    }

    public void handleEvent(MessageReceivedEvent event) {
        String command;
        try {
            command = event.getMessage().getContentRaw();
            command = command.substring(2);
        } catch (Exception e) {
            command = "";
        }
        if(notValid(event)) return;
        doCommand(command, event);
    }
}
