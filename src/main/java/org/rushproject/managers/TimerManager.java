package org.rushproject.managers;

import org.bukkit.World;
import org.rushproject.RushRemake;
import org.rushproject.utilities.GTimer;

import java.util.HashMap;
import java.util.Map;

public class TimerManager {

    private final RushRemake main;
    private final Map<World, GTimer> timers;

    public TimerManager(RushRemake main) {
        this.main = main;
        this.timers = new HashMap<>();
    }

    public void startTimer(World world) {
        if (timers.containsKey(world)) {
            GTimer timer = timers.get(world);
            timer.startTimer();
        }
    }

    public void resetTimer(World world) {
        if (timers.containsKey(world)) {
            GTimer timer = timers.get(world);
            timer.resetTimer();
        }
    }

    public GTimer getTimer(World world) {
        return timers.get(world);
    }

    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    // Rest of your code...
}