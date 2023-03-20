package me.monst.pluginutil.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * A command which is executable by a CommandSender.
 * An executable might perform an action itself, or delegate to a different executable based on the command arguments.
 * An executable can be guarded behind a {@link Permission} which must be owned by the executing player.
 */
public interface Executable {

    String getName();
    
    String getDescription();
    
    String getUsage();
    
    default boolean isPlayerOnly() {
        return false;
    }
    
    default Permission getPermission() {
        return Permission.NONE;
    }
    
    default String getNoPermissionMessage() {
        return "You do not have permission to do that.";
    }
    
    void execute(CommandSender sender, List<String> args) throws CommandExecutionException;
    
    default List<String> getTabCompletions(Player player, List<String> args) {
        return Collections.emptyList();
    }

}
