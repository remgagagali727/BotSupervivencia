package com.remgagagali727.discord.survplanet.controller;

import com.remgagagali727.discord.survplanet.entity.*;
import com.remgagagali727.discord.survplanet.repository.*;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigInteger;
import java.util.List;

@Controller
public class ItemController {

    @Autowired
    SpaceshipRepository spaceshipRepository;
    @Autowired
    CraftingRepository craftingRepository;
    @Autowired
    WeaponRepository weaponRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    DrillRepository drillRepository;
    @Autowired
    RodRepository rodRepository;
    @Autowired
    PlanetRepository planetRepository;
    @Autowired
    TypeRepository typeRepository;
    @Autowired
    LootRepository lootRepository;

    public void addItem(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Item i = new Item();
            i.setName(s[0]);
            i.setDescription(s[1]);
            i.setCrafting_price(s[2]);
            i.setSell_price(s[3]);
            new BigInteger(s[2]);
            new BigInteger(s[3]);
            event.getChannel().sendMessage("Chi che pudo").queue();
            itemRepository.save(i);
        } catch (Exception e) {
            UniverseController.invalidCommand(event);
        }
    }

    @Transactional
    public void addDrill(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Drill i = new Drill();
            Long id = Long.parseLong(s[0]);
            Item it = itemRepository.findById(id).get();
            i.setItem(it);
            i.setToughness(s[1]);
            new BigInteger(s[1]);
            drillRepository.save(i);
            event.getChannel().sendMessage("Chi che pudo").queue();
        } catch (Exception e) {
            System.out.println(e);
            UniverseController.invalidCommand(event);
        }
    }

    @Transactional
    public void addRod(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Rod i = new Rod();
            Long id = Long.parseLong(s[0]);
            Item it = itemRepository.findById(id).get();
            i.setItem(it);
            i.setToughness(s[1]);
            new BigInteger(s[1]);
            rodRepository.save(i);
            event.getChannel().sendMessage("Chi che pudo").queue();
        } catch (Exception e) {
            System.out.println(e);
            UniverseController.invalidCommand(event);
        }
    }

    @Transactional
    public void addWeapon(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Weapon i = new Weapon();
            Long id = Long.parseLong(s[0]);
            Item it = itemRepository.findById(id).get();
            i.setItem(it);
            i.setToughness(s[1]);
            new BigInteger(s[1]);
            weaponRepository.save(i);
            event.getChannel().sendMessage("Chi che pudo").queue();
        } catch (Exception e) {
            System.out.println(e);
            UniverseController.invalidCommand(event);
        }
    }

    @Transactional
    public void addSpaceship(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Spaceship i = new Spaceship();
            Long id = Long.parseLong(s[0]);
            Item it = itemRepository.findById(id).get();
            i.setItem(it);
            i.setSpeed(s[1]);
            new BigInteger(s[1]);
            spaceshipRepository.save(i);
            event.getChannel().sendMessage("Chi che pudo").queue();
        } catch (Exception e) {
            System.out.println(e);
            UniverseController.invalidCommand(event);
        }
    }

    @Transactional
    public void addLoot(String command, MessageReceivedEvent event) {
        try{
            String s[] = command.split(", ");

            Long pid = Long.parseLong(s[0]);
            Long iid = Long.parseLong(s[1]);
            Long tid = Long.parseLong(s[2]);
            new BigInteger(s[3]);

            Loot loot = new Loot();
            Planet planet = planetRepository.findById(pid).get();
            Item item = itemRepository.findById(iid).get();
            Type type = typeRepository.findById(tid).get();

            loot.setPlanet(planet);
            loot.setItem(item);
            loot.setAmount(s[3]);
            loot.setType(type);
            loot.setId(new Loot.LootId(planet.getId(), item.getId(), type.getId()));
            lootRepository.save(loot);
            event.getChannel().sendMessage("Chi che pudo, loot").queue();
        } catch (Exception e) {
            System.out.println(e);
            UniverseController.invalidCommand(event);
        }
    }

    public void items(String command, MessageReceivedEvent event) {
        long page;
        try {
            page = Long.parseLong(command);
        } catch (Exception ignored) {
            UniverseController.invalidCommand(event);
            return;
        }
        List<Item> planets = itemRepository.findAll();
        page = Long.min(page - 1, (planets.size() - 1) / 10);
        StringBuilder mes = new StringBuilder("**Items**\n");
        for(int i = (int) page * 10;i < Long.min((page + 1) * 10, planets.size());i++) {
            Item item = planets.get(i);
            String items = "(" + item.getId() + ") " + item.getName() + "\n";
            mes.append(items);
        }
        mes.append("Page ").append(page + 1);
        event.getChannel().sendMessage(mes.toString()).queue();
    }

    public void addRecipe(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Long id1 = Long.parseLong(s[0]);
            Long id2 = Long.parseLong(s[1]);
            new BigInteger(s[2]);

            Crafting crafting = new Crafting();
            Item i1 = itemRepository.findById(id1).get();
            Item i2 = itemRepository.findById(id2).get();
            crafting.setItem(i1);
            crafting.setRequired(i2);
            crafting.setAmount(s[2]);
            crafting.setId(new Crafting.CraftingId(i1.getId(), i2.getId()));

            craftingRepository.save(crafting);
            event.getChannel().sendMessage("You added a new recipe").queue();
        } catch (Exception e) {
            System.err.println("Error en getFood: " + e.getMessage());
            e.printStackTrace();

            event.getChannel().sendMessage("❌ Ocurrió un error al procesar la solicitud: " + e.getMessage()).queue();
        }
    }

    @Transactional
    public void setCraftingPrice(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Long id1 = Long.parseLong(s[0]);
            new BigInteger(s[1]);

            Item i1 = itemRepository.findById(id1).get();
            i1.setCrafting_price(s[1]);

            itemRepository.save(i1);
            event.getChannel().sendMessage("You changed the crafting price of an item").queue();
        } catch (Exception e) {
            System.err.println("Error en getFood: " + e.getMessage());
            e.printStackTrace();

            event.getChannel().sendMessage("❌ Ocurrió un error al procesar la solicitud: " + e.getMessage()).queue();
        }
    }

    public void setSellPrice(String command, MessageReceivedEvent event) {
        try {
            String[] s = command.split(", ");
            Long id1 = Long.parseLong(s[0]);
            new BigInteger(s[1]);

            Item i1 = itemRepository.findById(id1).get();
            i1.setSell_price(s[1]);

            itemRepository.save(i1);
            event.getChannel().sendMessage("You changed the crafting price of an item").queue();
        } catch (Exception e) {
            System.err.println("Error en getFood: " + e.getMessage());
            e.printStackTrace();
            event.getChannel().sendMessage("❌ Ocurrió un error al procesar la solicitud: " + e.getMessage()).queue();
        }
    }
}
