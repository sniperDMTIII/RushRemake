package org.rushproject.utilities;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.rushproject.RushRemake;

public class GTimer {

    private final World world;
    private final RushRemake main;
    private int timerSeconds;
    private BukkitTask timerTask;

    public GTimer(World world, RushRemake main) {
        this.world = world;
        this.main = main;
        this.timerSeconds = 0;
    }


    public void startTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }

        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                timerSeconds++;
            }
        }.runTaskTimer(main, 20, 20); // Increment timer every second
    }

    public void resetTimer() {
        timerSeconds = 0;
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    public int getTimerSeconds() {
        return timerSeconds;
    }

    public String getWorldName() {
        return world.getName();
    }
}
