package org.rushproject.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.rushproject.utilities.GState;

import java.util.*;

import static org.rushproject.managers.MapsManager.saveMaps;
import static org.rushproject.managers.MapsManager.setMap;

public class TeamManager {

    private static final Map<String, Map<String, List<String>>> worldTeams = new HashMap<>();

    private static final Map<String, Location> teamLocations = new HashMap<>();

    public static void addPlayerToTeam(String worldName, String playerName) {
        Map<String, List<String>> worldTeamMap = worldTeams.computeIfAbsent(worldName, k -> new HashMap<>());

        // Générer une équipe (rouge ou bleue) au hasard

        String randomteam = (new Random().nextInt(2) == 0) ? "red" : "blue";

        if (getTeamPlayerCount(worldName, "red") == 0 && getTeamPlayerCount(worldName, "blue") == 0)
        {
            worldTeamMap.computeIfAbsent(randomteam, k -> new ArrayList<>()).add(playerName);


        } else if (getTeamPlayerCount(worldName, "red") == 1 && getTeamPlayerCount(worldName, "blue") == 0) {
            addPlayerToSpecificTeam(worldName, playerName, "blue");

        }  else if (getTeamPlayerCount(worldName, "blue") == 1 && getTeamPlayerCount(worldName, "red") == 0) {
            addPlayerToSpecificTeam(worldName, playerName, "red");

        }  else if (getTeamPlayerCount(worldName, "red") <= 1 && getTeamPlayerCount(worldName, "blue") <= 1) {

            worldTeamMap.computeIfAbsent(randomteam, k -> new ArrayList<>()).add(playerName);

        }


    }

    public static void removePlayerFromTeam(String worldName, String playerName, String team) {
        Map<String, List<String>> worldTeamMap = worldTeams.get(worldName);
        if (worldTeamMap != null) {
            List<String> playerList = worldTeamMap.get(team);
            if (playerList != null) {
                playerList.remove(playerName);
            }
        }
    }

    public void clearTeam(String worldName, String team) {
        Map<String, List<String>> worldTeamMap = worldTeams.get(worldName);
        if (worldTeamMap != null) {
            worldTeamMap.remove(team);
        }
    }



    public static void addPlayerToSpecificTeam(String worldName, String playerName, String team) {
        Map<String, List<String>> worldTeamMap = worldTeams.computeIfAbsent(worldName, k -> new HashMap<>());
        worldTeamMap.computeIfAbsent(team, k -> new ArrayList<>()).add(playerName);
    }

    public static String getPlayerTeam(String worldName, String playerName) {
        Map<String, List<String>> worldTeamMap = worldTeams.get(worldName);
        if (worldTeamMap != null) {
            for (Map.Entry<String, List<String>> entry : worldTeamMap.entrySet()) {
                List<String> playerList = entry.getValue();
                if (playerList.contains(playerName)) {
                    return entry.getKey(); // Renvoie le nom de l'équipe
                }
            }
        }
        return null; // Le joueur n'est pas dans une équipe ou le monde n'est pas configuré
    }

    public static int getTeamPlayerCount(String worldName, String team) {
        Map<String, List<String>> worldTeamMap = worldTeams.get(worldName);
        if (worldTeamMap != null) {
            List<String> playerList = worldTeamMap.get(team);
            if (playerList != null) {
                return playerList.size();
            }
        }
        return 0; // Aucune équipe trouvée pour le monde ou équipe vide
    }

    public static void setTeamLocation(World world) {

        Location redbase = new Location(Bukkit.getWorld(world.getName()), -53, 4, 68, -90, 0);
        Location bluebase = new Location(Bukkit.getWorld(world.getName()), 8, 4, 6, 0, 0);

        teamLocations.put("red", redbase);
        teamLocations.put("blue", bluebase);
        // You might want to save this to a configuration file
    }

    // Method to get the location of a team
    public static Location getTeamLocation(String teamName) {
        return teamLocations.getOrDefault(teamName, null);
    }

    // Example method to teleport a player to their team's location
    public static void teleportPlayerToTeamLocation(World world, Player player) {
        String teamName = getPlayerTeam(world.getName(), player.getName());
        if (teamName != null) {
            Location teamLocation = getTeamLocation(teamName);
            if (teamLocation != null) {
                player.teleport(teamLocation);
            }
        }
    }


    public static ChatColor getTeamColor(String worldName, String playerName) {
        String playerTeam = getPlayerTeam(worldName, playerName);
        if (playerTeam != null) {
            if ("red".equalsIgnoreCase(playerTeam)) {
                return ChatColor.RED;
            } else if ("blue".equalsIgnoreCase(playerTeam)) {
                return ChatColor.BLUE;
            }
            // Ajoutez d'autres couleurs et équipes si nécessaire
        }
        return ChatColor.RESET; // Aucune équipe ou couleur par défaut
    }




}
