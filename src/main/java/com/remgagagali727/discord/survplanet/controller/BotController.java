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
            case "equip":
                playerController.equip("", event);
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
                return;
            case "crafts":
                universeController.crafts("1", event);
                return;
            case "sell":
                playerController.sell("", event);
                return;
            case "planet":
                planetController.planetInfo("", event);
                return;
            case "item":
                itemController.itemInfo("", event);
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
        if(command.startsWith("crafts ")) universeController.crafts(command.substring(7), event);
        if(command.startsWith("sell ")) playerController.sell(command.substring(5), event);
        if(command.startsWith("planet ")) planetController.planetInfo(command.substring(7), event);
        if(command.startsWith("item ")) itemController.itemInfo(command.substring(5), event);
    }

    private void help(MessageReceivedEvent event) {
        String helpMessage = """
                📖 **SurvPlanet Command Guide**
                Use `s!(command)` to execute a command.

                🔹 **Player Information**
                `profile`, `p` → View your player profile (health, coins, equipped items, etc.)
                `inventory (page)`, `i (page)` → View your inventory with pagination

                🔹 **Actions on Your Current Planet**
                `mine`, `m` → Mine for resources
                `fish`, `f` → Catch fish
                `hunt`, `h` → Hunt for materials or food
                `eat (food)` → Consume food to restore health
                `craft (recipe)` → Craft items using available materials
                `equip (item)` → Equip an item (e.g., weapon, drill, fishing rod)

                🔹 **Exploration**
                `go (planet)` → Travel to a different planet (if not already there)
                `planet` → View detailed info about your current planet
                `planets (page)` → View the list of available planets

                🔹 **Economy & Casino**
                `sell (item) (amount)` → Sell an item for coins (must be on a planet)
                `casino (number)`, `cas (number)` → Bet your coins at the casino (only works if you're on the casino planet)

                🔹 **General Info**
                `items (page)` → Browse all available items
                `item (item name)` → View detailed information about a specific item
                `crafts (page)` → View the list of available crafting recipes
                `help` → Show this help menu

                ℹ️ Do not include parentheses when using commands. Example: `s!mine`, `s!go earth`, or `s!sell fish 5`

                🌌 Explore planets, gather resources, and grow stronger in **SurvPlanet**!
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
