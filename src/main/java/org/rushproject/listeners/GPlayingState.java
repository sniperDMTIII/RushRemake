package org.rushproject.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.rushproject.RushRemake;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TeamManager;
import org.rushproject.utilities.GMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GPlayingState implements Listener {

    private final RushRemake main;

    public GPlayingState(RushRemake main) {
        this.main = main;
    }

    private final Map<Player, List<Player>> damageSources = new HashMap<>();


    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {

        Player player = (Player) event.getEntity();
        World world = player.getWorld();
        String teamName = TeamManager.getPlayerTeam(world.getName(), player.getName());
        Location teamLocation = TeamManager.getTeamLocation(teamName);

        Location respawnLocation = getRespawnLocation(player, world);
        double newHealth = player.getHealth() - event.getFinalDamage();

        List<Player> sources = damageSources.getOrDefault(player, new ArrayList<>());

        if (MapsManager.isPlayerInConfiguredWorld(player) && respawnLocation != null) {
            if (newHealth <= 0) {
                if (teamLocation != null) {
                    boolean bedsExist = blockInZone(teamLocation, teamLocation, 10);
                    if (!bedsExist) {
                        event.setCancelled(true);
                        player.teleport(teamLocation);
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage("§6[Rush] §eVous avez été éliminé !");
                        player.sendMessage("§6[Rush] §eContinuez de participez en tant que spectateur !");
                        TeamManager.removePlayerFromTeam(world.getName(), player.getName(), teamName);
                        if (TeamManager.getTeamPlayerCount(world.getName(), "red") == 0 || TeamManager.getTeamPlayerCount(world.getName(), "blue") == 0) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (MapsManager.isPlayerInWorld(p, world)) {
                                    p.setGameMode(GameMode.SPECTATOR);
                                    if (TeamManager.getTeamPlayerCount(world.getName(), "red") == 0) {
                                        p.sendTitle("§6§lPartie terminée", "§fL'équipe §9Bleu§f a remporté la partie !", 0, 500, 100);
                                        GFinishState.clearandresetmap(world);
                                    } else if (TeamManager.getTeamPlayerCount(world.getName(), "blue") == 0) {
                                        p.sendTitle("§6§lPartie terminée", "§fL'équipe §cRouge§f a remporté la partie !", 0, 500, 100);
                                        GFinishState.clearandresetmap(world);
                                    }


                                }
                            }
                        }
                } else {
                        event.setCancelled(true);
                        player.setInvulnerable(true);
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(teamLocation);
                        player.setHealth(player.getMaxHealth());
                        player.setFlySpeed(0);
                        if (sources.isEmpty()) {
                            GMessage.DeathVoidMessage(player, world);
                        } else if (sources.stream().allMatch(source -> source instanceof Player)) {
                            Player attackingPlayer = sources.stream()
                                    .filter(source -> source instanceof Player)
                                    .findFirst()
                                    .orElse(null);
                            GMessage.KillMessage(player, world, attackingPlayer);
                        } else {
                            GMessage.KillMessage(player, world, null); // Modify this according to your needs
                        }

                        sources.clear();

                        new BukkitRunnable() {
                            int count = 5;

                            @Override
                            public void run() {
                                if (count <= 0) {
                                    player.setInvulnerable(false);
                                    player.setGameMode(GameMode.SURVIVAL);
                                    player.spigot().respawn();
                                    player.teleport(respawnLocation);
                                    event.setCancelled(true);
                                    player.setFlySpeed(0.3f);
                                    GStartingState.equipPlayerForStarting(player);
                                    cancel();
                                } else {
                                    player.sendTitle("", "§eRespawn dans §6" + count + "§e secondes", 0, 20, 0);
                                    count--;
                                }
                            }
                        }.runTaskTimer(main, 0, 20L); // 1 tick delay to ensure player has fully died
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();
        Location blockLocation = event.getBlock().getLocation();
        Player player = event.getPlayer();
        World world = event.getBlock().getWorld();
        String teamname = TeamManager.getPlayerTeam(world.getName(), player.getName());

        if (MapsManager.isPlayerInConfiguredWorld(player)) {
            // Liste des types de blocs autorisés
            if (blockType != Material.TNT && blockType != Material.SANDSTONE && blockType != Material.BED_BLOCK && blockType != Material.BED) {
                event.setCancelled(true);

            } else if (blockType == Material.BED || blockType == Material.BED_BLOCK) {
                Location teamBedLocation = TeamManager.getTeamLocation(teamname);
                Location teamredLocation = TeamManager.getTeamLocation("red");
                Location teamblueLocation = TeamManager.getTeamLocation("blue");
                event.setDropItems(false);
                if (teamBedLocation != null && blockLocation.distanceSquared(teamredLocation) <= 100) {
                    if (teamredLocation == teamBedLocation) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("Vous ne pouvez pas casser le lit de votre équipe !");
                    } else if (teamblueLocation == teamBedLocation) {

                        GMessage.BedTeamDestroy(player, world, "§cRouge", "red");

                    }
                } else if (teamBedLocation != null && blockLocation.distanceSquared(teamblueLocation) <= 100){
                    if (teamblueLocation == teamBedLocation) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("Vous ne pouvez pas casser le lit de votre équipe !");
                    } else if (teamredLocation == teamBedLocation) {

                        GMessage.BedTeamDestroy(player, world, "§9Bleu", "blue");

                    }

                } else GMessage.BedDestroy(player, world);



            }
        }
    }


    public boolean blockInZone(Location blockLocation, Location zoneCenter, double radius) {
        World world = zoneCenter.getWorld();
        int centerX = zoneCenter.getBlockX();
        int centerY = zoneCenter.getBlockY();
        int centerZ = zoneCenter.getBlockZ();

        int blockX = blockLocation.getBlockX();
        int blockY = blockLocation.getBlockY();
        int blockZ = blockLocation.getBlockZ();

        if (world == null) {
            return false;
        }

        if (Math.abs(blockX - centerX) <= radius && Math.abs(blockY - centerY) <= radius && Math.abs(blockZ - centerZ) <= radius) {
            // Check if the block is a bed
            Block block = world.getBlockAt(blockX, blockY, blockZ);
            return block.getType() == Material.BED || block.getType() == Material.BED_BLOCK;
        }

        return false;
    }

    // Utilisation de la méthode isBlockInZone


    @EventHandler
    public void onPlayerKilled(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        World world = event.getEntity().getWorld();

        if (MapsManager.isPlayerInConfiguredWorld(player)) {
            if (TeamManager.getPlayerTeam(world.getName(), player.getName()).equals(TeamManager.getPlayerTeam(world.getName(), damager.getName()))) {
                event.setCancelled(true);
            } else {
                List<Player> sources = damageSources.computeIfAbsent(player, k -> new ArrayList<>());

                if (!sources.contains(damager)) {
                    sources.add(0, damager);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        World world = event.getClickedBlock().getWorld();

        if (MapsManager.isPlayerInConfiguredWorld(player)) {
                if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    int maxHeight =  13;
                    if (block.getLocation().getBlockY() > maxHeight) {
                        event.setCancelled(true);
                    }
                }
            }
        }

    private final Map<Player, Boolean> isPlayerNearTarget = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location targetLocation = new Location(player.getWorld(), 41, 6, 102); // Remplacez x, y, z par les coordonnées de la position cible
        World world = player.getWorld();
        boolean msg = true;

        boolean isNearTarget = player.getLocation().distanceSquared(targetLocation) <= 85 * 85;

        if (MapsManager.isPlayerInConfiguredWorld(player)) {
            double distanceSquared = player.getLocation().distanceSquared(targetLocation);
            if (distanceSquared <= 85 * 85) {
                if (msg && !isPlayerNearTarget.containsKey(player)) {
                    player.sendMessage("§cTu ne peux pas aller dans cette direction !");
                    isPlayerNearTarget.put(player, true);
                } else if (!isNearTarget) {
                    isPlayerNearTarget.remove(player);
                }

                new BukkitRunnable() {
                    double damage = 1.5; // Montant de dégâts infligés à chaque itération

                    @Override
                    public void run() {
                        if (!player.isOnline() || player.getLocation().distanceSquared(targetLocation) > 85 * 85) {
                            cancel(); // Arrête la boucle si le joueur n'est plus en ligne ou s'éloigne de la position cible
                        }

                        Location respawnLocation = getRespawnLocation(player, world);
                        if (player.getHealth() - damage <= 0) {
                            player.setHealth(0);
                            cancel();
                            new BukkitRunnable() {
                                int count = 5;

                                @Override
                                public void run() {
                                    if (count <= 0) {
                                        player.setInvulnerable(false);
                                        player.setGameMode(GameMode.SURVIVAL);
                                        player.spigot().respawn();
                                        player.teleport(respawnLocation);
                                        event.setCancelled(true);
                                        player.setWalkSpeed(0.2f);
                                        cancel();
                                    } else {
                                        player.sendTitle("", "§eRespawn dans §6" + count + "§e secondes", 0, 20, 0);
                                        count--;
                                    }
                                }
                            }.runTaskTimer(main, 0, 20L); // 1 tick delay to ensure player has fully died
                        } else {
                            player.damage(damage); // Inflige les dégâts au joueur
                        }
                    }
                }.runTaskTimer(main, 0, 20L);
            }
        }
    }









public static Location getRespawnLocation(Player player, World world) {
    Location redbase = new Location(Bukkit.getWorld(world.getName()), -53, 5, 68, 91.3f, 5.7f);
    Location bluebase = new Location(Bukkit.getWorld(world.getName()), 8, 5, 6, -177f, 4.3f);
    if (TeamManager.getPlayerTeam(world.getName(), player.getName()) == "red") {
        return redbase;
    }else return bluebase;
}

}
