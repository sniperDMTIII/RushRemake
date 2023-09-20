package org.rushproject.listeners.Placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.rushproject.RushRemake;
import org.rushproject.listeners.GPlayingState;
import org.rushproject.listeners.GWaitingState;
import org.rushproject.managers.MapsManager;
import org.rushproject.managers.TeamManager;
import org.rushproject.managers.TimerManager;
import org.rushproject.utilities.GState;
import org.rushproject.utilities.GTimer;



public class GPlaceholders extends PlaceholderExpansion {

    private  RushRemake main;



    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public GPlaceholders(RushRemake main) {
        this.main = main;
    }

    public GPlayingState gplay = new GPlayingState(main);

    @Override
    public String getAuthor() {
        return "Sniper4Ever";
    }

    @Override
    public String getIdentifier() {
        return "rushremake";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        /* 40 */     return true;
        /*    */   }

    @Override
    public boolean persist() {
        /* 45 */     return true;
        /*    */   }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        Player player = p.getPlayer();

        if(params.equalsIgnoreCase("team")) {
                return TeamManager.getPlayerTeam(player.getPlayer().getWorld().getName(), player.getName());
        }
        if(params.equalsIgnoreCase("team_number_red")) {
            String worldname = player.getPlayer().getWorld().getName();
            Integer number = TeamManager.getTeamPlayerCount(worldname, "red");
            return String.valueOf(number);

            }

        if(params.equalsIgnoreCase("team_number_blue")) {
            String worldname = player.getPlayer().getWorld().getName();
            Integer number = TeamManager.getTeamPlayerCount(worldname, "blue");
            return String.valueOf(number);

        }

        if (params.startsWith("team_bed_blue")) {
            Location Lteam = TeamManager.getTeamLocation("blue");
            Boolean bedteam = gplay.blockInZone(Lteam, Lteam, 10);
            if (Lteam == null) {
                return "";
            }

            if (bedteam) {
                return "§7✔";

            } else return "§7✘";

        }

        if (params.startsWith("team_bed_red")) {
            Location Lteam = TeamManager.getTeamLocation("red");
            Boolean bedteam = gplay.blockInZone(Lteam, Lteam, 10);
            if (Lteam == null) {
                return "";
            }

            if (bedteam) {
                return "§7✔";

            } else return "§7✘";

        }


        return null; // Placeholder not found
    }
}

