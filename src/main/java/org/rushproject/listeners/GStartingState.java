package org.rushproject.listeners;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.rushproject.RushRemake;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TeamManager;
import org.rushproject.utilities.GSpawners;
import org.rushproject.utilities.GState;

public class GStartingState extends BukkitRunnable {


    private final int totalTime;
    private int remainingTime;
    private final World world;

    private final RushRemake main;


    public GStartingState(int totalTime, World world, RushRemake main) {
        this.remainingTime = totalTime;
        this.totalTime = totalTime;
        this.world = world;
        this.main = main;

    }



    @Override
    public void run() {
        if (remainingTime > 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (MapsManager.isPlayerInWorld(player, world)) {
                    if (remainingTime == 30 || remainingTime == 10 || remainingTime == 5 || remainingTime == 4 || remainingTime == 3 || remainingTime == 2 ) {
                        player.sendMessage("§6[Rush] §eLa partie commence dans §a" + remainingTime + "s");
                    } else if (remainingTime == 1) {

                        Location redbase = new Location(Bukkit.getWorld(world.getName()), -53, 5, 68, -90, 0);
                        Location bluebase = new Location(Bukkit.getWorld(world.getName()), 8, 5, 6, 0, 0);

                        TeamManager.setTeamLocation(world);

                        player.sendMessage("§6[Rush] §eLa partie commence dans §a" + remainingTime + "s");
                        player.sendMessage("§6[Rush] §aLa partie démarre !");
                        player.setWalkSpeed(0.3f);
                        MapsManager.setMap(world, GState.PLAYING);
                        main.getTimerManager().startTimer(world);
                        equipPlayerForStarting(player);
                        if (TeamManager.getPlayerTeam(world.getName(), player.getName()) == "red") {
                            //player.teleport(redbase);
                            TeamManager.teleportPlayerToTeamLocation(world,player);

                            player.setCollidable(false);
                            player.setBedSpawnLocation(redbase);
                        } else {
                            //player.teleport(bluebase);
                            TeamManager.teleportPlayerToTeamLocation(world,player);
                            TeamManager.setTeamLocation(world);
                            player.setBedSpawnLocation(bluebase);

                        }
                    }
                }
            }
            remainingTime--;
        } else {
            this.cancel();
        }
    }

    public static void equipPlayerForStarting(Player player) {
        PlayerInventory inventory = player.getInventory();

        // Clear inventory and set game mode
        inventory.clear();
        player.setGameMode(GameMode.SURVIVAL);

        // Equip armor and weapons
        inventory.setHelmet(new ItemStack(Material.IRON_HELMET));
        inventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        inventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        inventory.setBoots(new ItemStack(Material.IRON_BOOTS));

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        sword.addEnchantment(Enchantment.KNOCKBACK, 1);
        inventory.setItem(0, sword);

        inventory.setItem(1, new ItemStack(Material.IRON_PICKAXE, 1));

        // Fill inventory with sandstone
        ItemStack sandstoneStack = new ItemStack(Material.SANDSTONE, 64);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == 0 || i == 1 || (i >= 36 && i <= 39)) {
                continue;
            }
            inventory.setItem(i, sandstoneStack);
        }
    }
}
