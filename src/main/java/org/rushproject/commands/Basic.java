package org.rushproject.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.rushproject.managers.MapsManager;
import org.rushproject.utilities.ColorHelper;
import org.rushproject.utilities.GState;

public class Basic implements CommandExecutor {

    private final MapsManager mapsManager;

    public Basic(MapsManager mapsManager) {

        this.mapsManager = mapsManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        sender.sendMessage(ColorHelper.MENU_BAR);
        sender.sendMessage("  §6§lRush Plugin Menu");
        sender.sendMessage("");
        sender.sendMessage("  §6Creator:§e Sniper4Ever");
        sender.sendMessage("  §6/rush help§e for show the helping menu");
        sender.sendMessage(ColorHelper.MENU_BAR);

        if (command.getName().equalsIgnoreCase("rush") && args.length > 0) {
            if (args[0].equalsIgnoreCase("addmap") && sender instanceof Player) {
                Player player = (Player) sender;
                mapsManager.addMap(player.getWorld()); // Remplacez ETAT_1 par l'état souhaité
                sender.sendMessage("Map " + player.getWorld() + " ajoutée à la HashMap !");



                return false;
            }
        }
        return false;
    }
}
