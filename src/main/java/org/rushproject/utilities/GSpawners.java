package org.rushproject.utilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GSpawners {

    private static List<Location> spawnLocations;


    private static int secondsPassed = 0;

    //                        Location red1 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 71);
//                        Location red2 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 69);
//                        Location red3 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 67);
//                        Location red4 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 65);
//
//                        Location i11 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 132);
//                        Location i12 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 134);
//                        Location i13 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 136);
//                        Location i14 = new  Location(Bukkit.getWorld(world.getName()), -58, 4, 138);
//
//                        Location i21 = new  Location(Bukkit.getWorld(world.getName()), 5, 4, 200);
//                        Location i22 = new  Location(Bukkit.getWorld(world.getName()), 7, 4, 200);
//                        Location i23 = new  Location(Bukkit.getWorld(world.getName()), 9, 4, 200);
//                        Location i24 = new  Location(Bukkit.getWorld(world.getName()), 11, 4, 200);
//
//                        Location i31 = new  Location(Bukkit.getWorld(world.getName()), 77, 4, 200);
//                        Location i32 = new  Location(Bukkit.getWorld(world.getName()), 75, 4, 200);
//                        Location i33 = new  Location(Bukkit.getWorld(world.getName()), 73, 4, 200);
//                        Location i34 = new  Location(Bukkit.getWorld(world.getName()), 71, 4, 200);
//
//                        Location i41 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 138);
//                        Location i42 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 136);
//                        Location i43 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 134);
//                        Location i44 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 132);
//
//                        Location i51 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 71);
//                        Location i52 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 69);
//                        Location i53 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 67);
//                        Location i54 = new  Location(Bukkit.getWorld(world.getName()), 140, 4, 65);
//
//                        Location i61 = new  Location(Bukkit.getWorld(world.getName()), 77, 4, 1);
//                        Location i62 = new  Location(Bukkit.getWorld(world.getName()), 75, 4, 1);
//                        Location i63 = new  Location(Bukkit.getWorld(world.getName()), 73, 4, 1);
//                        Location i64 = new  Location(Bukkit.getWorld(world.getName()), 72, 4, 1);
//
//                        Location blue1 = new  Location(Bukkit.getWorld(world.getName()), 11, 4, 6);
//                        Location blue2 = new  Location(Bukkit.getWorld(world.getName()), 9, 4, 6);
//                        Location blue3 = new  Location(Bukkit.getWorld(world.getName()), 7, 4, 6);
//                        Location blue4 = new  Location(Bukkit.getWorld(world.getName()), 5, 4, 6);


    public void GSpawners(List<Location> spawnLocations) {
        this.spawnLocations = spawnLocations;
    }

    public void run() {
        spawnBricks();
        secondsPassed++;


        if (secondsPassed % 30 == 0) {
            spawnIronIngot();
        }
    }

    private static void spawnBricks() {
        for (Location location : spawnLocations) {
            location.getWorld().dropItem(location, new ItemStack(Material.BRICK));
        }
    }

    private static void spawnIronIngot() {
        for (Location location : spawnLocations) {
            location.getWorld().dropItem(location, new ItemStack(Material.IRON_INGOT));
        }
    }
}
