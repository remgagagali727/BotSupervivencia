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
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.util.Iterator;
import java.util.Optional;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        try {
            // Mensaje de depuración inmediato para confirmar que el método se está ejecutando
            event.getChannel().sendMessage("🍽️ Procesando solicitud de comida...").queue();
            
            // Verificar si tabla food existe
            boolean foodTableExists = false;
            try {
                List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'food'");
                foodTableExists = !tables.isEmpty();
            } catch (Exception e) {
                System.err.println("Error verificando tabla food: " + e.getMessage());
            }

            if (!foodTableExists) {
                event.getChannel().sendMessage(
                        "¡Sistema de comida no inicializado! Por favor, espera a que el administrador lo configure.")
                        .queue();
                return;
            }

            // Obtener jugador
            Long userId = event.getAuthor().getIdLong();
            Player player = playerController.getPlayer(userId);
            
            // Configurar valores predeterminados
            String foodName = "Space Apple";
            int quantity = 1;
            
            // Procesar el comando para extraer nombre y cantidad
            if (command.startsWith("getfood ")) {
                String[] parts = command.substring(8).trim().split(" ");
                StringBuilder nameBuilder = new StringBuilder();
                
                // Extraer el último elemento como posible cantidad
                String lastPart = parts[parts.length - 1];
                boolean lastPartIsNumber = false;
                
                try {
                    quantity = Integer.parseInt(lastPart);
                    lastPartIsNumber = true;
                    
                    // Limitar la cantidad entre 1 y 5
                    if (quantity < 1 || quantity > 5) {
                        quantity = 1;
                    }
                } catch (NumberFormatException e) {
                    // El último elemento no es un número, es parte del nombre
                }
                
                // Construir el nombre
                for (int i = 0; i < (lastPartIsNumber ? parts.length - 1 : parts.length); i++) {
                    nameBuilder.append(parts[i]).append(" ");
                }
                
                String extractedName = nameBuilder.toString().trim();
                if (!extractedName.isEmpty()) {
                    foodName = extractedName;
                }
            }
            
            // Imprimir mensaje de lo que estamos buscando
            event.getChannel().sendMessage("Buscando: " + foodName + " (Cantidad: " + quantity + ")").queue();
            
            // Buscar alimento en la base de datos
            List<Map<String, Object>> foods = jdbcTemplate.queryForList(
                    "SELECT i.id, i.name, f.health_added FROM item i " +
                    "JOIN food f ON i.id = f.id " +
                    "WHERE LOWER(i.name) LIKE ?", 
                    "%" + foodName.toLowerCase() + "%");
            
            if (foods.isEmpty()) {
                event.getChannel().sendMessage("No se encontró ningún alimento llamado \"" + foodName + 
                        "\". Prueba con: Space Apple, Cosmic Bread, Alien Steak, Nebula Soup, Mars Chocolate").queue();
                return;
            }
            
            // Tomar el primer alimento encontrado
            Map<String, Object> foodData = foods.get(0);
            Long itemId = ((Number) foodData.get("id")).longValue();
            String itemName = (String) foodData.get("name");
            String healthAdded = (String) foodData.get("health_added");
            
            // Obtener el item completo
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            if (optionalItem.isEmpty()) {
                event.getChannel().sendMessage("Error interno: No se pudo obtener el item con ID " + itemId).queue();
                return;
            }
            
            Item foodItem = optionalItem.get();
            
            // Agregar el alimento al inventario
            for (int i = 0; i < quantity; i++) {
                player.getInventory().add(foodItem);
            }
            
            // Guardar el jugador
            playerController.savePlayer(player);
            
            // Mensaje de confirmación con embed
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle("🎁 ¡Alimento obtenido!")
                    .setDescription("Has recibido " + quantity + " " + itemName)
                    .addField("Restaura", "+" + healthAdded + " HP", true)
                    .addField("Usa", "s!eat " + itemName + " para consumirlo", true)
                    .setFooter("Space Survival Bot", null);
            
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
            
        } catch (Exception e) {
            System.err.println("Error en getFood: " + e.getMessage());
            e.printStackTrace();
            
            // Asegurar que siempre se envíe un mensaje al chat incluso si hay error
            event.getChannel().sendMessage("❌ Ocurrió un error al procesar la solicitud: " + e.getMessage()).queue();
        }
    }

}