package org.rushproject.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.rushproject.RushRemake;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TeamManager;
import org.rushproject.utilities.GState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

public class GFinishState {

    private static RushRemake main;

    public GFinishState(RushRemake main) {
        this.main = main;

    }


    public static void clearandresetmap(World world) {
        World spawnWorld = Bukkit.getWorld("SpawnServ"); // Remplacez par le nom de votre monde de spawn

        new BukkitRunnable() {
            @Override
            public void run() {
                pasteRandomSchematic(new Location(Bukkit.getWorld(world.getName()), 41, 5, 102));
                for (Player player : Bukkit.getOnlinePlayers()) {

                    if (MapsManager.isPlayerInWorld(player, world)) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.SURVIVAL);
                        player.teleport(spawnWorld.getSpawnLocation());

                        String team = TeamManager.getPlayerTeam(world.getName(), player.getName());
                        TeamManager.removePlayerFromTeam(world.getName(), player.getName(), team);
                        MapsManager.removePlayerFromWorldList(world.getName(), player.getName());
                        MapsManager.setMap(world, GState.WAITING);

                    }
                }
            }
        }.runTaskLater(main, 120 * 20L); // Convertit les secondes en ticks
    }

    public static void pasteRandomSchematic(Location pasteLocation) {

        String schematicFileName = "rushmap.schematic"; // Nom du fichier de schéma
        File schematicFile = new File(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder(), "schematics/" + schematicFileName);

        ClipboardFormat format = ClipboardFormat.findByFile(schematicFile);

        if (format == null) {
            // Gérer le cas où le format du schéma n'est pas pris en charge
            return;
        }

        // Choisir un joueur au hasard parmi les joueurs en ligne
        Player randomPlayer = getRandomOnlinePlayer();
        Location oldlocation = randomPlayer.getLocation();

        if (randomPlayer == null) {
            // Gérer le cas où aucun joueur n'est en ligne
            return;
        }

        // Téléporter le joueur à l'emplacement de collage
        randomPlayer.teleport(pasteLocation);

        // Charger et coller le schéma en utilisant les commandes WorldEdit
        try {
            randomPlayer.performCommand("/schem load " + schematicFileName);
            randomPlayer.performCommand("/paste");
            randomPlayer.teleport(oldlocation);
        } catch (Exception e) {
            randomPlayer.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
            e.printStackTrace();
        }
    }



    private static Player getRandomOnlinePlayer() {
        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        if (onlinePlayers.length == 0) {
            return null;
        }
        int randomIndex = new Random().nextInt(onlinePlayers.length);
        return onlinePlayers[randomIndex];
    }
}












