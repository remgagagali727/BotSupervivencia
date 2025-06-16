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
    WeaponRepository weaponRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    DrillRepository drillRepository;
    @Autowired
    RodRepository rodRepository;

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
}
