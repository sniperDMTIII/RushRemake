package org.rushproject.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rushproject.RushRemake;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TeamManager;
import org.rushproject.utilities.GState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GWaitingState implements Listener {

    private final RushRemake main;
    private final List<Player> players = new ArrayList<>();

    public List<Player> getPlayers() {
        return players;
    }

    public GWaitingState(RushRemake main) {
        this.main = main;
    }

    private final Map<World, GStartingState> countdowns = new HashMap<>();




    @EventHandler
    public void onJoinMap(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer();

        String worldName = player.getWorld().getName();
        String fromWorldName = event.getFrom().getName();
        World world = player.getWorld();
        World fromWorld = event.getFrom();

        ItemStack redWool = new ItemStack(Material.WOOL,1, DyeColor.RED.getWoolData());
        ItemMeta redWoolMeta = redWool.getItemMeta();
        redWoolMeta.setDisplayName("§cRejoindre l'équipe Rouge");
        redWoolMeta.setUnbreakable(true);
        redWool.setItemMeta(redWoolMeta);

        ItemStack blueWool = new ItemStack(Material.WOOL,1, DyeColor.BLUE.getWoolData());
        ItemMeta blueWoolMeta = blueWool.getItemMeta();
        blueWoolMeta.setDisplayName("§1Rejoindre l'équipe Bleu");
        blueWoolMeta.setUnbreakable(true);
        blueWool.setItemMeta(blueWoolMeta);
        //-51 5 68 91.3 5.7
        // 8 4 7 -177 4.3


        Location waitingspawn = new Location(Bukkit.getWorld(worldName), 41, 127, 102, -87.4f, 3.5f);
        Location redbase = new Location(Bukkit.getWorld(worldName), -51, 5, 68, 91.3f, 5.7f);
        Location bluebase = new Location(Bukkit.getWorld(worldName), 8, 4, 7, -177f, 4.3f);


        if (MapsManager.isPlayerInConfiguredWorld(player)) {

            if (MapsManager.getState(worldName).equals("WAITING") || MapsManager.getState(worldName).equals("STARTING") ) {

                MapsManager.addPlayerToWorldList(worldName, player.getName());

                TeamManager.addPlayerToTeam(worldName, player.getName());
                ChatColor teamColor = TeamManager.getTeamColor(worldName, player.getName());
                String formattedName = teamColor + player.getName();
                player.setPlayerListName(formattedName);

                player.getInventory().clear();

                player.getInventory().setItem(0, redWool);
                player.getInventory().setItem(1, blueWool);

                player.teleport(waitingspawn);
                player.setFoodLevel(20);

                if (MapsManager.getPlayerCountInWorld(worldName) == 1) {
                    for (Player p : world.getPlayers()) {
                        p.sendMessage("§6[Rush] §e" + player.getName() + " §ea rejoint la partie ! §c(" + MapsManager.getPlayerCountInWorld(worldName)  + "/8)");
                    }

                } else if (MapsManager.getPlayerCountInWorld(worldName) == 2) {
                    for (Player p : world.getPlayers()) {
                        p.sendMessage("§6[Rush] §e" + player.getName() + " §ea rejoint la partie ! §a(" + MapsManager.getPlayerCountInWorld(worldName)  + "/8)");
                        MapsManager.setMap(player.getWorld(), GState.WAITING);

                        GStartingState countdown = countdowns.get(world);
                        if (countdown != null) {
                            countdown.cancel();
                        }
                        countdown = new GStartingState(30, world, main);
                        countdowns.put(world, countdown);
                        countdown.runTaskTimer(main, 0, 20L);
                    }
                    
                } else if (MapsManager.getPlayerCountInWorld(worldName) > 2) {
                    for (Player p : world.getPlayers()) {
                        p.sendMessage("§6[Rush] §e" + player.getName() + " §ea rejoint la partie ! §a(" + MapsManager.getPlayerCountInWorld(worldName)  + "/8)");
                    }
                //player.sendMessage("§6[Rush] §e" + player.getName() + " §ea rejoint la partie ! " + MapsManager.getPlayerCountInWorld(worldName)  + "§e Joueurs dans la partie !");
            }

        }
    } else if (MapsManager.isWorldConfigured(fromWorldName)) {
            if (MapsManager.getState(fromWorldName).equals("WAITING") || MapsManager.getState(fromWorldName).equals("STARTING")) {
                MapsManager.removePlayerFromWorldList(fromWorldName, player.getName());
                TeamManager.removePlayerFromTeam(fromWorldName, player.getName(), TeamManager.getPlayerTeam(fromWorldName, player.getName()));
                for (Player p : fromWorld.getPlayers()) {
                    if (MapsManager.getState(fromWorldName).equals("WAITING")) {
                        //p.sendMessage("§6[Rush] §e" + player.getName() + " §ea quitté la partie ! §a(" + MapsManager.getPlayerCountInWorld(fromWorldName) + "/8)");
                    } else if (MapsManager.getState(fromWorldName).equals("STARTING")){
                        p.sendMessage("§6[Rush] §e" + player.getName() + " §ea quitté la partie ! §c(" + MapsManager.getPlayerCountInWorld(fromWorldName) + "/8)");
                        p.sendMessage("§6[Rush] §cLancement annulé !");
                        GStartingState countdown = countdowns.get(fromWorld);
                        if (countdown != null) {
                            countdown.cancel();
                        }
                        MapsManager.setMap(fromWorld, GState.WAITING);

                    }

                }

            }
        }


    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        World world = player.getWorld();
        String worldName = world.getName();

        if (MapsManager.isPlayerInConfiguredWorld(player)) {
            if (MapsManager.getState(worldName).equals("WAITING")) {

                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {

                    if (item != null && item.getType() == Material.WOOL && item.getItemMeta().getDisplayName().equals("§cRejoindre l'équipe Rouge")) {

                        String playerTeam = TeamManager.getPlayerTeam(worldName, player.getName());
                        if (playerTeam == "blue") {
                            // Ajouter le joueur à l'équipe rouge
                            TeamManager.addPlayerToSpecificTeam(worldName, player.getName(), "red");
                            TeamManager.removePlayerFromTeam(worldName, player.getName(), "blue");

                            ChatColor teamColor = TeamManager.getTeamColor(worldName, player.getName());
                            String formattedName = teamColor + player.getName();
                            player.setPlayerListName(formattedName);

                            player.sendMessage("Vous avez rejoint l'équipe §cRouge !");

                        } else {
                            player.sendMessage("Tu es déja dans l'équipe §1cRouge !");
                        }
                    }
                } else if (item != null && item.getType() == Material.WOOL && item.getItemMeta().getDisplayName().equals("§9Rejoindre l'équipe Bleu")) {
                    if (MapsManager.isPlayerInConfiguredWorld(player)) {
                        String playerTeam = TeamManager.getPlayerTeam(worldName, player.getName());
                        if (playerTeam == "red") {
                            // Ajouter le joueur à l'équipe rouge
                            TeamManager.addPlayerToSpecificTeam(worldName, player.getName(), "blue");
                            TeamManager.removePlayerFromTeam(worldName, player.getName(), "red");

                            ChatColor teamColor = TeamManager.getTeamColor(worldName, player.getName());
                            String formattedName = teamColor + player.getName();
                            player.setPlayerListName(formattedName);

                            player.sendMessage("Vous avez rejoint l'équipe §1Bleu !");

                        } else {
                            player.sendMessage("Tu es déja dans l'équipe §1Bleu !");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        World world = player.getWorld();

    }
            

}




