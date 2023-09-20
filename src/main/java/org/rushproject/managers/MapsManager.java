package org.rushproject.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.rushproject.RushRemake;
import org.rushproject.utilities.GState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsManager {

    private static JavaPlugin plugin;
    private static  RushRemake main;
    private static Map<String, String> mapsData;
    private static final Map<String, List<String>> worldPlayerLists = new HashMap<>();
    //private static final HashMap<World, List<Player>> worldPlayers = new HashMap<>();

    public MapsManager(JavaPlugin plugin, Map<String, String> mapsData) {
        this.plugin = plugin;
        this.mapsData = mapsData;
    }

    private GState state;
    public static Map<World, GState> maps = new HashMap<>();

    public MapsManager() {
    }


    public void addMap(World world) {
        maps.put(world, GState.WAITING);
        saveMaps();


    }



    public static boolean isState(World world, GState state) {
        if (maps.containsKey(world)) {
            return maps.get(world) == state;
        }
        return false;
    }

    public static String getState(String worldName) {
        return mapsData.get(worldName);
    }

    public static void setMap(World world, GState state) {
        maps.put(world, state);
        saveMaps();


    }

    public static int getPlayerCountInWorld(String worldName) {
        List<String> playerList = worldPlayerLists.get(worldName);
        if (playerList != null) {
            return playerList.size();
        }
        return 0;
    }

    public static boolean isWorldConfigured(String worldName) {
        return mapsData.containsKey(worldName);
    }

    public static boolean isPlayerInWorld(Player player, World world) {
        List<String> playerList = worldPlayerLists.get(world.getName());
        return playerList != null && playerList.contains(player.getName());
    }


    public static void saveMaps() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<World, GState> entry : maps.entrySet()) {
            config.set("maps." + entry.getKey().getName(), entry.getValue().toString());
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPlayerInConfiguredWorld(Player player) {
        String worldName = player.getWorld().getName();
        return mapsData.containsKey(worldName);
    }

    public static void addPlayerToWorldList(String worldName, String playerName) {
        List<String> playerList = worldPlayerLists.computeIfAbsent(worldName, k -> new ArrayList<>());
        if (!playerList.contains(playerName)) {
            playerList.add(playerName);
        }
    }

    public static void removePlayerFromWorldList(String worldName, String playerName) {
        List<String> playerList = worldPlayerLists.get(worldName);
        if (playerList != null) {
            playerList.remove(playerName);
        }
    }

    public void initializeMaps() {
        for (World world : Bukkit.getWorlds()) {
            if (isWorldConfigured(world.getName())) {
                setMap(world, GState.WAITING);
                saveMaps();
            }
        }
        saveMaps();
    }

    public void chargerMaps() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (config.contains("maps")) {
            ConfigurationSection mapsSection = config.getConfigurationSection("maps");
            for (String mondeNom : mapsSection.getKeys(false)) {
                World monde = plugin.getServer().getWorld(mondeNom);
                if (monde != null) {
                    GState etat = GState.valueOf(mapsSection.getString(mondeNom));
                    maps.put(monde, etat);
                }
            }
        }

    }


}
