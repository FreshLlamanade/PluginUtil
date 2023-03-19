package me.monst.pluginutil.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface PlayerExecutable extends Executable {
    
    @Override
    default boolean isPlayerOnly() {
        return true;
    }
    
    @Override
    default void execute(CommandSender sender, Args args) throws CommandException {
        execute((Player) sender, args);
    }
    
    void execute(Player player, Args args) throws CommandException;
    
}
