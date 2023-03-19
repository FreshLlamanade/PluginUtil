package me.monst.pluginutil.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public interface TopLevelExecutable extends CommandExecutor, TabCompleter, Executable {
    
    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (getPermission().notOwnedBy(sender)) {
            sender.sendMessage(ChatColor.DARK_RED + getNoPermissionMessage());
            return true;
        }
        try {
            execute(sender, new Args(args));
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }
    
    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || !(sender instanceof Player) || getPermission().notOwnedBy(sender))
            return Collections.emptyList();
        return getTabCompletions((Player) sender, new Args(args));
    }
    
}
