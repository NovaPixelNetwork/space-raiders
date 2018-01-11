package net.novapixelnetwork.gamecore.entity;

import net.novapixelnetwork.spaceraiders.SpaceRaiders;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class CooldownCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Player only command
        if(sender instanceof Player){
            Player p = (Player) sender;
            //Get the amount of milliseconds that have passed since the feature was last used.
            Long timeLeft = System.currentTimeMillis() - CooldownManager.INSTANCE.getCooldown(p.getUniqueId());
            if(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= CooldownManager.DEFAULT_COOLDOWN){
                p.sendMessage(ChatColor.GREEN + "Featured used!");
                CooldownManager.INSTANCE.setCooldown(p.getUniqueId(), System.currentTimeMillis());
            }else{
                p.sendMessage(ChatColor.RED.toString() + TimeUnit.MILLISECONDS.toSeconds(timeLeft) + " seconds before you can use this feature again.");
            }
        }else{
            sender.sendMessage("Player-only command");
        }

        return true;
    }

}
