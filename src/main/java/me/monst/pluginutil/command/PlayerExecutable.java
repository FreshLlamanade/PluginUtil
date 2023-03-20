package me.monst.pluginutil.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface PlayerExecutable extends Executable {
    
    @Override
    default boolean isPlayerOnly() {
        return true;
    }
    
    @Override
    default void execute(CommandSender sender, List<String> args) throws CommandExecutionException {
        execute((Player) sender, args);
    }
    
    void execute(Player player, List<String> args) throws CommandExecutionException;
    
}
