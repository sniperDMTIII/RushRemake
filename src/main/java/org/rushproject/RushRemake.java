package org.rushproject;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.rushproject.commands.Basic;
import org.rushproject.listeners.GGeneralState;
import org.rushproject.listeners.GPlayingState;
import org.rushproject.listeners.GWaitingState;
import org.rushproject.listeners.Placeholders.GPlaceholders;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TimerManager;

import java.util.HashMap;
import java.util.Map;

public final class RushRemake extends JavaPlugin {

    private MapsManager mapsManager;

    private TimerManager timerManager;

    @Override
    public void onEnable() {

        new GPlaceholders(this).register();

        saveDefaultConfig();

        this.timerManager = new TimerManager(this);
        MapsManager mapsManager = new MapsManager();


        System.out.println("[RushRemake] Plugin loaded succesfully");
        Basic basicCommand = new Basic(mapsManager);
        getCommand("rush").setExecutor(basicCommand);

        //mapsManager.initializeMaps();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new GWaitingState(this), this);
        pm.registerEvents(new GPlayingState(this), this);
        pm.registerEvents(new GGeneralState(this), this);

        ConfigurationSection mapsSection = getConfig().getConfigurationSection("maps");
        Map<String, String> mapsData = new HashMap<>();

        if (mapsSection != null) {
            for (String worldName : mapsSection.getKeys(false)) {
                String state = mapsSection.getString(worldName);
                mapsData.put(worldName, state);
            }
        }

        mapsManager = new MapsManager(this, mapsData);
        // Plugin startup logic


            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        } else if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            
        }

    }

    public TimerManager getTimerManager() {
        return timerManager;
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
