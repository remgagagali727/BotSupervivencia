package com.remgagagali727.discord.survplanet.config;

import com.remgagagali727.discord.survplanet.entity.*;
import com.remgagagali727.discord.survplanet.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class SimpleInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private DrillRepository drillRepository;
    
    @Autowired
    private RodRepository rodRepository;
    
    @Autowired
    private WeaponRepository weaponRepository;
    
    @Autowired
    private SpaceshipRepository spaceshipRepository;
    
    @Autowired
    private PlanetRepository planetRepository;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private FoodRepository foodRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verificar si ya existen los elementos básicos
        Optional<Item> basicDrill = itemRepository.findById(0L);
        if (basicDrill.isPresent()) {
            System.out.println("Los datos básicos ya están inicializados.");
            return;
        }
        
        // Reiniciar secuencias y crear objetos con IDs específicos
        resetSequences();
        createBasicItems();
        createBasicEquipment();
        createEarthPlanet();
        createFoodItems(); // Nuevo método para crear alimentos
        
        System.out.println("Inicialización completa - ¡Objetos básicos creados con éxito!");
    }
    
    private void resetSequences() {
        try (Connection conn = dataSource.getConnection()) {
            // Establecer la secuencia en 4 para que los próximos IDs sean mayores que nuestros IDs manuales
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT setval('item_id_seq', 4, false)")) {
                stmt.execute();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT setval('planet_id_seq', 3, false)")) {
                stmt.execute();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT setval('type_id_seq', 1, false)")) {
                stmt.execute();
            }
            System.out.println("Secuencias reiniciadas");
        } catch (Exception e) {
            System.err.println("Error al reiniciar secuencias: " + e.getMessage());
        }
    }
    
    private void createBasicItems() {
        try {
            // Insertar items directamente con IDs específicos
            jdbcTemplate.update("INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (0, 'Basic Drill', 'A simple drilling tool', '50', '10')");
            jdbcTemplate.update("INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (1, 'Basic Rod', 'A simple fishing rod', '50', '10')");
            jdbcTemplate.update("INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (2, 'Basic Weapon', 'A simple weapon', '50', '10')");
            jdbcTemplate.update("INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (3, 'Basic Spaceship', 'A simple spaceship', '100', '25')");
            
            System.out.println("Items básicos creados");
        } catch (Exception e) {
            System.err.println("Error creando items básicos: " + e.getMessage());
            throw e;
        }
    }
    
    private void createBasicEquipment() {
        try {
            // Crear equipamiento asociado a los items
            jdbcTemplate.update("INSERT INTO drill (id, toughness) VALUES (0, '1')");
            jdbcTemplate.update("INSERT INTO rod (id, toughness) VALUES (1, '1')"); 
            jdbcTemplate.update("INSERT INTO weapon (id, toughness) VALUES (2, '1')");
            jdbcTemplate.update("INSERT INTO spaceship (id, speed) VALUES (3, '1')");
            
            System.out.println("Equipamiento básico creado");
        } catch (Exception e) {
            System.err.println("Error creando equipamiento: " + e.getMessage());
            throw e;
        }
    }
    
    private void createEarthPlanet() {
        try {
            // Crear planeta Tierra con ID 2
            jdbcTemplate.update("INSERT INTO planet (id, name, x, y, toughness) VALUES (2, 'Earth', '0', '0', '1')");
            System.out.println("Planeta Tierra creado con ID 2");
        } catch (Exception e) {
            System.err.println("Error creando planeta Tierra: " + e.getMessage());
        }
    }
    
    private void createFoodItems() {
        try {
            // Crear tipo "food" si no existe
            jdbcTemplate.update("INSERT INTO type (type) VALUES ('food') ON CONFLICT (type) DO NOTHING");
            
            // Obtener el ID del tipo "food"
            Long foodTypeId = jdbcTemplate.queryForObject(
                "SELECT id FROM type WHERE type = 'food'", Long.class);
                
            // Crear alimentos y asociarlos con el tipo "food"
            createFoodItem("Space Apple", "Una fruta crujiente que brilla con destellos estelares", "20", "5", "10", foodTypeId);
            createFoodItem("Cosmic Bread", "Pan suave infundido con energía estelar", "30", "10", "15", foodTypeId);
            createFoodItem("Alien Steak", "Carne exótica de una extraña criatura. Alta en proteínas", "100", "40", "50", foodTypeId);
            createFoodItem("Nebula Soup", "Sopa caliente que brilla con los colores de una nebulosa", "50", "20", "30", foodTypeId);
            createFoodItem("Mars Chocolate", "Dulce hecho con cacao marciano", "25", "8", "12", foodTypeId);
            createFoodItem("Emergency Ration", "Ración espacial estándar. No sabrosa pero eficiente", "15", "5", "20", foodTypeId);
            createFoodItem("Crystal Fruit", "Fruta rara con pulpa cristalina que brilla", "200", "80", "75", foodTypeId);
            
            System.out.println("Alimentos creados correctamente");
        } catch (Exception e) {
            System.err.println("Error creando alimentos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createFoodItem(String name, String description, String buyPrice, String sellPrice, String healthAdded, Long typeId) {
        try {
            // Comprobar si ya existe este alimento
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM item WHERE name = ?", Integer.class, name);
                
            if (count > 0) {
                System.out.println("El alimento " + name + " ya existe.");
                return;
            }
            
            // Crear item
            jdbcTemplate.update("INSERT INTO item (name, description, buy_price, sell_price) VALUES (?, ?, ?, ?)",
                name, description, buyPrice, sellPrice);
                
            // Obtener el ID generado
            Long itemId = jdbcTemplate.queryForObject(
                "SELECT id FROM item WHERE name = ?", Long.class, name);
                
            // Crear entrada en food
            jdbcTemplate.update("INSERT INTO food (id, heal, health_added) VALUES (?, ?, ?)",
                itemId, "true", healthAdded);
                
            // Asociar con el tipo "food"
            jdbcTemplate.update("INSERT INTO item_types (item_id, types_type) VALUES (?, ?)",
                itemId, typeId);
                
            System.out.println("Alimento creado: " + name + " (ID: " + itemId + ")");
        } catch (Exception e) {
            System.err.println("Error creando alimento " + name + ": " + e.getMessage());
        }
    }
}