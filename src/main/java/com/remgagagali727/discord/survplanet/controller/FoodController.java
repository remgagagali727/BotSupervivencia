package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Food;
import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.entity.Player;
import com.remgagagali727.discord.survplanet.repository.FoodRepository;
import com.remgagagali727.discord.survplanet.repository.ItemRepository;
import com.remgagagali727.discord.survplanet.repository.PlayerRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.util.Iterator;
import java.util.Optional;
import java.util.List;

@Controller
public class FoodController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private PlayerController playerController;

    public void eat(String command, MessageReceivedEvent event) {
        if (command.startsWith("eat "))
            command = command.substring(4);

        if (command.isEmpty()) {
            showHelpEmbed(event);
            return;
        }

        String foodName = command;
        Long userId = event.getAuthor().getIdLong();

        Player player = playerController.getPlayer(userId);

        if (!player.isOnPlanet()) {
            player.notInPlanet(event);
            return;
        }

        Item foodItem = null;
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(foodName)) {
                foodItem = item;
                break;
            }
        }

        if (foodItem == null) {
            event.getChannel().sendMessage("No tienes " + foodName + " en tu inventario.").queue();
            return;
        }

        // Verificar si el item es comida
        Optional<Food> optFood = foodRepository.findById(foodItem.getId());
        if (optFood.isEmpty()) {
            event.getChannel().sendMessage("¡" + foodName + " no es comestible!").queue();
            return;
        }

        Food food = optFood.get();

        
        int currentHealth = Integer.parseInt(player.getHealth());
        int maxHealth = 100;
        int healthToAdd = Integer.parseInt(food.getHealth_added());
        int newHealth = Math.min(currentHealth + healthToAdd, maxHealth);
        player.setHealth(String.valueOf(newHealth));

        Iterator<Item> iterator = player.getInventory().iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getId().equals(foodItem.getId())) {
                iterator.remove();
                break;
            }
        }

        playerController.savePlayer(player);
        sendEatEmbed(player, food, currentHealth, newHealth, event);
    }

    private void sendEatEmbed(Player player, Food food, int oldHealth, int newHealth, MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("🍽️ ¡Yum!")
                .setDescription("Has consumido " + food.getItem().getName())
                .addField("Salud Restaurada", "+" + food.getHealth_added() + " HP", true)
                .addField("Salud Actual", oldHealth + " → " + newHealth + " HP", true)
                .setFooter("Space Survival Bot", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void showHelpEmbed(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("🍽️ Comando de Comida")
                .setDescription("Consume alimentos para restaurar salud.")
                .addField("Uso", "s!eat <nombre_del_alimento>", false)
                .addField("Ejemplo", "s!eat space_apple", false)
                .setFooter("Space Survival Bot", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public void getFood(String command, MessageReceivedEvent event) {
        Long userId = event.getAuthor().getIdLong();
        Player player = playerController.getPlayer(userId);

        // Obtener el nombre del alimento (default: Space Apple)
        String foodName = "Space Apple";
        int quantity = 1;

        if (command.startsWith("getfood ")) {
            String input = command.substring(8).trim();
            StringBuilder nameBuilder = new StringBuilder();
            String quantityStr = "";

            boolean foundNumber = false;
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);

                if (Character.isDigit(c) && (i == 0 || input.charAt(i - 1) == ' ')) {
                    foundNumber = true;
                    quantityStr = input.substring(i);
                    break;
                }

                if (!foundNumber) {
                    nameBuilder.append(c);
                }
            }

            // Procesar el nombre del alimento
            if (nameBuilder.length() > 0) {
                foodName = nameBuilder.toString().trim();
            }

            // Procesar la cantidad
            if (foundNumber) {
                try {
                    quantity = Integer.parseInt(quantityStr.trim());
                    if (quantity < 1 || quantity > 5) {
                        quantity = 1;
                    }
                } catch (NumberFormatException e) {
                    // aqui nada
                }
            }
        }

        List<Food> foods = foodRepository.findAll();
        Food selectedFood = null;
        Item foodItem = null;

        for (Food food : foods) {
            if (food.getItem().getName().toLowerCase().contains(foodName.toLowerCase())) {
                selectedFood = food;
                foodItem = food.getItem();
                break;
            }
        }

        if (foodItem == null) {
            event.getChannel()
                    .sendMessage("¡No se encontró el alimento: " + foodName
                            + "! Prueba con: Space Apple, Cosmic Bread, Alien Steak, Nebula Soup, Mars Chocolate")
                    .queue();
            return;
        }

        // Agregar el alimento al inventario del jugador
        for (int i = 0; i < quantity; i++) {
            player.getInventory().add(foodItem);
        }

        // Guardar el jugador
        playerController.savePlayer(player);

        // Confirmar al usuario
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("🎁 ¡Alimento obtenido!")
                .setDescription("Has recibido " + quantity + " " + foodItem.getName())
                .addField("Restaura", "+" + selectedFood.getHealth_added() + " HP", true)
                .addField("Usa", "s!eat " + foodItem.getName() + " para consumirlo", true)
                .setFooter("Space Survival Bot", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

}