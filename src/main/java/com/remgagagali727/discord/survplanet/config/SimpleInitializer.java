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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            System.out.println("Iniciando verificación de base de datos...");
            
            // Verificar y crear tablas si es necesario
            verifyAndCreateTables();
            
            // Verificar si ya existe el equipo básico
            Optional<Item> basicDrill = itemRepository.findById(0L);
            if (basicDrill.isPresent()) {
                System.out.println("Equipo básico ya inicializado. Verificando datos de comida...");
                
                // Verificar si hay alimentos, si no, crearlos
                if (!foodExists()) {
                    System.out.println("Creando alimentos...");
                    createFoodItems();
                } else {
                    System.out.println("Alimentos ya inicializados.");
                }
                
                return;
            }
            
            // Inicializar todo desde cero
            System.out.println("Iniciando creación de datos básicos...");
            
            // Reiniciar secuencias para IDs manuales
            resetSequences();
            
            // Crear objetos básicos
            createBasicItems();
            createBasicEquipment();
            createEarthPlanet();
            createFoodItems();
            
            System.out.println("Inicialización completa - ¡Datos básicos creados con éxito!");
            
        } catch (Exception e) {
            System.err.println("Error en inicialización: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relanzar para que Spring sepa que hubo un error
        }
    }
    
    private void verifyAndCreateTables() {
        try {
            // Verificar tabla type
            if (!tableExists("type")) {
                System.out.println("Tabla 'type' no existe. Creándola...");
                jdbcTemplate.execute(
                    "CREATE TABLE type (" +
                    "id SERIAL PRIMARY KEY," +
                    "type VARCHAR(255) UNIQUE" +
                    ")"
                );
            }
            
            // Verificar tabla food
            if (!tableExists("food")) {
                System.out.println("Tabla 'food' no existe. Creándola...");
                jdbcTemplate.execute(
                    "CREATE TABLE food (" +
                    "id BIGINT PRIMARY KEY," +
                    "heal VARCHAR(255)," +
                    "health_added VARCHAR(255)," +
                    "CONSTRAINT fk_food_item FOREIGN KEY (id) REFERENCES item(id)" +
                    ")"
                );
            }
            
            // Verificar tabla item_types para relaciones muchos a muchos
            if (!tableExists("item_types")) {
                System.out.println("Tabla 'item_types' no existe. Creándola...");
                jdbcTemplate.execute(
                    "CREATE TABLE item_types (" +
                    "item_id BIGINT," +
                    "types_type BIGINT," +
                    "PRIMARY KEY (item_id, types_type)," +
                    "CONSTRAINT fk_item_types_item FOREIGN KEY (item_id) REFERENCES item(id)," +
                    "CONSTRAINT fk_item_types_type FOREIGN KEY (types_type) REFERENCES type(id)" +
                    ")"
                );
            }
            
            System.out.println("Verificación de tablas completada.");
        } catch (Exception e) {
            System.err.println("Error verificando/creando tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean tableExists(String tableName) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, "public", tableName.toLowerCase(), new String[] {"TABLE"});
            return tables.next();
        } catch (SQLException e) {
            System.err.println("Error verificando existencia de tabla " + tableName + ": " + e.getMessage());
            return false;
        }
    }
    
    private boolean foodExists() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM food", Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void resetSequences() {
        try {
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS item_id_seq RESTART WITH 100");
            jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS planet_id_seq RESTART WITH 10");
            System.out.println("Secuencias reiniciadas");
        } catch (Exception e) {
            System.err.println("Error al reiniciar secuencias (no crítico): " + e.getMessage());
        }
    }
    
    private void createBasicItems() {
        try {
            System.out.println("Creando items básicos...");
            
            jdbcTemplate.update(
                "INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (?, ?, ?, ?, ?)",
                0, "Basic Drill", "A simple drilling tool", "50", "10"
            );
            
            jdbcTemplate.update(
                "INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (?, ?, ?, ?, ?)",
                1, "Basic Rod", "A simple fishing rod", "50", "10"
            );
            
            jdbcTemplate.update(
                "INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (?, ?, ?, ?, ?)",
                2, "Basic Weapon", "A simple weapon", "50", "10"
            );
            
            jdbcTemplate.update(
                "INSERT INTO item (id, name, description, buy_price, sell_price) VALUES (?, ?, ?, ?, ?)",
                3, "Basic Spaceship", "A simple spaceship", "100", "25"
            );
            
            System.out.println("Items básicos creados");
        } catch (Exception e) {
            System.err.println("Error creando items básicos: " + e.getMessage());
            throw e;
        }
    }
    
    private void createBasicEquipment() {
        try {
            System.out.println("Creando equipamiento básico...");
            
            jdbcTemplate.update("INSERT INTO drill (id, toughness) VALUES (?, ?)", 0, "1");
            jdbcTemplate.update("INSERT INTO rod (id, toughness) VALUES (?, ?)", 1, "1");
            jdbcTemplate.update("INSERT INTO weapon (id, toughness) VALUES (?, ?)", 2, "1");
            jdbcTemplate.update("INSERT INTO spaceship (id, speed) VALUES (?, ?)", 3, "1");
            
            System.out.println("Equipamiento básico creado");
        } catch (Exception e) {
            System.err.println("Error creando equipamiento: " + e.getMessage());
            throw e;
        }
    }
    
    private void createEarthPlanet() {
        try {
            System.out.println("Creando planeta Tierra...");
            
            if (!planetRepository.existsById(2L)) {
                jdbcTemplate.update(
                    "INSERT INTO planet (id, name, x, y, toughness) VALUES (?, ?, ?, ?, ?)",
                    2, "Earth", "0", "0", "1"
                );
                System.out.println("Planeta Tierra creado con ID 2");
            } else {
                System.out.println("El planeta Tierra ya existe.");
            }
        } catch (Exception e) {
            System.err.println("Error creando planeta Tierra: " + e.getMessage());
        }
    }
    
    private void createFoodItems() {
        try {
            System.out.println("Creando alimentos...");
            
            // Crear tipo "food" si no existe
            Long foodTypeId = createFoodType();
            
            // Crear alimentos básicos
            createFood("Space Apple", "Una fruta crujiente que brilla con destellos estelares", "20", "5", "10", foodTypeId);
            createFood("Cosmic Bread", "Pan suave infundido con energía estelar", "30", "10", "15", foodTypeId);
            createFood("Alien Steak", "Carne exótica de una extraña criatura. Alta en proteínas", "100", "40", "50", foodTypeId);
            createFood("Nebula Soup", "Sopa caliente que brilla con los colores de una nebulosa", "50", "20", "30", foodTypeId);
            createFood("Mars Chocolate", "Dulce hecho con cacao marciano", "25", "8", "12", foodTypeId);
            createFood("Emergency Ration", "Ración espacial estándar. No sabrosa pero eficiente", "15", "5", "20", foodTypeId);
            createFood("Crystal Fruit", "Fruta rara con pulpa cristalina que brilla", "200", "80", "75", foodTypeId);
            
            System.out.println("Alimentos creados con éxito");
        } catch (Exception e) {
            System.err.println("Error creando alimentos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Long createFoodType() {
        try {
            // Verificar si el tipo "food" ya existe
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM type WHERE type = ?", Integer.class, "food");
                
            if (count != null && count > 0) {
                // Obtener el ID del tipo existente
                return jdbcTemplate.queryForObject(
                    "SELECT id FROM type WHERE type = ?", Long.class, "food");
            }
            
            // Crear nuevo tipo "food"
            jdbcTemplate.update("INSERT INTO type (type) VALUES (?)", "food");
            
            // Obtener el ID generado
            return jdbcTemplate.queryForObject(
                "SELECT id FROM type WHERE type = ?", Long.class, "food");
        } catch (Exception e) {
            System.err.println("Error creando tipo 'food': " + e.getMessage());
            throw e;
        }
    }
    
    private void createFood(String name, String description, String buyPrice, String sellPrice, String healthAdded, Long typeId) {
        try {
            // Verificar si el alimento ya existe
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM item WHERE name = ?", Integer.class, name);
                
            if (count != null && count > 0) {
                System.out.println("El alimento " + name + " ya existe.");
                return;
            }
            
            // Insertar item
            jdbcTemplate.update(
                "INSERT INTO item (name, description, buy_price, sell_price) VALUES (?, ?, ?, ?)",
                name, description, buyPrice, sellPrice
            );
            
            // Obtener el ID generado
            Long itemId = jdbcTemplate.queryForObject(
                "SELECT id FROM item WHERE name = ?", Long.class, name
            );
            
            // Insertar food
            jdbcTemplate.update(
                "INSERT INTO food (id, heal, health_added) VALUES (?, ?, ?)",
                itemId, "true", healthAdded
            );
            
            // Asociar con tipo "food"
            jdbcTemplate.update(
                "INSERT INTO item_types (item_id, types_type) VALUES (?, ?)",
                itemId, typeId
            );
            
            System.out.println("Alimento creado: " + name + " (ID: " + itemId + ")");
        } catch (Exception e) {
            System.err.println("Error creando alimento " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}