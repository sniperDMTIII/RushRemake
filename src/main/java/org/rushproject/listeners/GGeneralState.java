package org.rushproject.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.rushproject.RushRemake;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TeamManager;
import org.rushproject.utilities.GState;

public class GGeneralState implements Listener {

    private RushRemake main;

    public GGeneralState(RushRemake main) {
        this.main = main;
    }


    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();
        World fromWorld = event.getFrom();
        World toWorld = player.getWorld();


        if (MapsManager.isWorldConfigured(fromWorld.getName())) {
            if (!MapsManager.isWorldConfigured(toWorld.getName())) {
                if (MapsManager.getState(fromWorld.getName()).equals("PLAYING")) {
                    TeamManager.removePlayerFromTeam(fromWorld.getName(), player.getName(), TeamManager.getPlayerTeam(fromWorld.getName(), player.getName()));
                    if (TeamManager.getTeamPlayerCount(fromWorld.getName(), "red") == 0 || TeamManager.getTeamPlayerCount(fromWorld.getName(), "blue") == 0) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (MapsManager.isPlayerInWorld(player, fromWorld)) {
                                p.setGameMode(GameMode.SPECTATOR);
                                if (TeamManager.getTeamPlayerCount(fromWorld.getName(), "red") == 0) {
                                    p.sendTitle("§6§lPartie terminée", "§fL'équipe §cRouge§f a déclaré forfait !", 0, 100, 10);
                                    GFinishState.clearandresetmap(fromWorld);
                                } else if (TeamManager.getTeamPlayerCount(fromWorld.getName(), "blue") == 0) {
                                    p.sendTitle("§6§lPartie terminée", "§fL'équipe §9Bleu§f a déclaré forfait !", 0, 100, 10);
                                    GFinishState.clearandresetmap(fromWorld);
                                }
                            }
                        }

                    }

                }


            }
        } else if (!MapsManager.isWorldConfigured(fromWorld.getName())) {
            if (MapsManager.isWorldConfigured(toWorld.getName())) {
                if (MapsManager.getState(fromWorld.getName()).equals("PLAYING")) {
                    TeamManager.addPlayerToTeam(toWorld.getName(), player.getName());
                    MapsManager.addPlayerToWorldList(toWorld.getName(), player.getName());
                    TeamManager.teleportPlayerToTeamLocation(toWorld, player);
                    player.setGameMode(GameMode.SPECTATOR);
                    new BukkitRunnable() {
                        int count = 5;

                        Location respawnLocation = GPlayingState.getRespawnLocation(player, toWorld);

                        @Override
                        public void run() {
                            if (count <= 0) {
                                player.setInvulnerable(false);
                                player.setGameMode(GameMode.SURVIVAL);
                                player.spigot().respawn();
                                player.teleport(respawnLocation);
                                player.setWalkSpeed(0.2f);
                                cancel();
                            } else {
                                player.sendTitle("", "§eApparition dans §6" + count + "§e secondes", 0, 20, 0);
                                count--;
                            }
                        }
                    }.runTaskTimer(main, 0, 20L); // 1 tick delay to ensure player has fully died


                }



            }

        }


    }
}
