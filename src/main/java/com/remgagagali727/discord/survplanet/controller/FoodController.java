package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.Food;
import com.remgagagali727.discord.survplanet.entity.Item;
import com.remgagagali727.discord.survplanet.entity.ItemRelation;
import com.remgagagali727.discord.survplanet.entity.Player;
import com.remgagagali727.discord.survplanet.repository.FoodRepository;
import com.remgagagali727.discord.survplanet.repository.ItemRelationRepository;
import com.remgagagali727.discord.survplanet.repository.ItemRepository;
import com.remgagagali727.discord.survplanet.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

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
    private ItemRelationRepository itere;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void eat(String command, MessageReceivedEvent event) {
        if (command.startsWith("eat "))
            command = command.substring(4);

        if (command.isEmpty()) {
            showHelpEmbed(event);
            return;
        }

        String foodName = command.trim();
        Long userId = event.getAuthor().getIdLong();

        Player player = playerController.getPlayer(userId);

        if (!player.isOnPlanet()) {
            player.notInPlanet(event);
            return;
        }

        Item foodItem = null;
        for (ItemRelation relation : player.getInventory()) {
            Item item = relation.getItem();
            if (item.getName().equalsIgnoreCase(foodName) && !relation.getAmount().equals("0")) {
                foodItem = item;
                break;
            }
        }

        if (foodItem == null) {
            event.getChannel().sendMessage("No tienes " + foodName + " en tu inventario.").queue();
            return;
        }

        Optional<Food> optFood = foodRepository.findById(foodItem.getId());
        if (optFood.isEmpty()) {
            event.getChannel().sendMessage("¡" + foodName + " no es comestible!").queue();
            return;
        }

        Food food = optFood.get();

        BigInteger currentHealth = new BigInteger(player.getHealth());
        BigInteger maxHealth = new BigInteger(player.getMaxHealth());

        BigInteger healToAdd = new BigInteger(food.getHeal());
        BigInteger maxHealthToAdd = new BigInteger(food.getHealth_added());

        BigInteger newMaxHealth = maxHealth.add(maxHealthToAdd);
        BigInteger newHeal = healToAdd.add(currentHealth).min(newMaxHealth);

        player.setHealth(newHeal.toString());
        player.setMaxHealth(newMaxHealth.toString());

        Iterator<ItemRelation> iterator = player.getInventory().iterator();
        while (iterator.hasNext()) {
            ItemRelation relation = iterator.next();
            Item item = relation.getItem();
            if (item.getId().equals(foodItem.getId())) {
                relation.setAmount(new BigInteger(relation.getAmount()).add(new BigInteger("-1")).toString());
                break;
            }
        }

        playerController.savePlayer(player);
        sendEatEmbed(player, food, currentHealth, newHeal, maxHealth, newMaxHealth, event);
    }

    private void sendEatEmbed(Player player, Food food, BigInteger oldHealth, BigInteger newHealth, BigInteger oldMaxHealth, BigInteger newMaxHealth, MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("🍽️ ¡Yum!")
                .setDescription("You've eaten " + food.getItem().getName())
                .addField("Health", oldHealth + " → " + newHealth + " :light_blue_heart:", true)
                .addField("MaxHealth", oldMaxHealth + " -> " + newMaxHealth + " :light_blue_heart:", true)
                .setFooter("Surv Planet", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void showHelpEmbed(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("🍽️ Comando de Comida")
                .setDescription("Consume alimentos para restaurar salud.")
                .addField("Uso", "s!eat <nombre_del_alimento>", false)
                .addField("Ejemplo", "s!eat space_apple", false)
                .setFooter("Surv Planet", null);

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @Transactional
    public void getFood(String command, MessageReceivedEvent event) {
        try {
            event.getChannel().sendMessage("🍽️ Procesando solicitud de comida...").queue();
            List<Food> foods = foodRepository.findAll();
            Food food = foods.get((int)(Math.random() * foods.size()));
            playerController.addToInventory(itemRepository.findById(food.getId()).get(), "1", event);
            event.getChannel().sendMessage("You got " + food.getItem().getName()).queue();
        } catch (Exception e) {
            System.err.println("Error en getFood: " + e.getMessage());
            e.printStackTrace();

            event.getChannel().sendMessage("❌ Ocurrió un error al procesar la solicitud: " + e.getMessage()).queue();
        }
    }

    @Transactional
    public void addFood(String command, MessageReceivedEvent event) {
        String[] s = Arrays.stream(command.split(", "))
                .map(String::trim)
                .toArray(String[]::new);
        try {
            Long iid = Long.parseLong(s[0]);
            Item item = itemRepository.findById(iid)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));

            new BigInteger(s[1]);
            new BigInteger(s[2]);

            Food food = new Food();
            food.setItem(item);
            food.setHeal(s[1]);
            food.setHealth_added(s[2]);

            foodRepository.save(food);

            event.getChannel().sendMessage("Se pudo: " + item.getName() + " ahora es una comida.").queue();
        } catch (Exception e) {
            System.out.println(e);
            UniverseController.invalidCommand(event);
        }
    }
}