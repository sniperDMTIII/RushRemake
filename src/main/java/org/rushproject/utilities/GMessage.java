package org.rushproject.utilities;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TeamManager;

public class GMessage {
    public static void DeathVoidMessage(Player killed, World world) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MapsManager.isPlayerInWorld(player, world)) {
                player.sendMessage("§6[Rush] §c " + killed.getName() + "§7a été tué par le vide");
            }


        }

    }

    public static void KillMessage(Player killed, World world, Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MapsManager.isPlayerInWorld(player, world)) {
                player.sendMessage("§6[Rush] §c" + killed.getName() + "§7a été tué par §a" + p.getName());
            }


        }

    }

    public static void BedDestroy(Player p, World world) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MapsManager.isPlayerInWorld(player, world)) {
                player.sendMessage("§6[Rush] §e" + p.getName() + "§fa détruit un lit intermédiaire !" );
            }


        }

    }

    public static void gamejoin(Player p, World world) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MapsManager.isPlayerInWorld(player, world)) {
                player.sendMessage("§6[Rush] §e" + p.getName() + "§fa rejoint la partie !" );
            }


        }

    }

    public static void BedTeamDestroy(Player p, World world, String teamname, String team) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (MapsManager.isPlayerInWorld(player, world)) {
                player.sendMessage("§6[Rush] §e" + p.getName() + "§fa détruit le lit de l'équipe " + teamname );
                if (TeamManager.getPlayerTeam(world.getName(), player.getName()) == team) {
                    player.sendTitle("", "§c✘ Votre lit a été détruit ! ✘", 0, 500, 100);
                }
            }


        }

    }
}
