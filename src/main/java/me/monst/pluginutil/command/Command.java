package me.monst.pluginutil.command;

import me.monst.pluginutil.command.exception.CommandExecutionException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * A command which can be executed by a CommandSender.
 * An executable might perform an action itself, or delegate to a different executable based on the command arguments.
 * An executable can be guarded behind a {@link Permission} which must be owned by the executor.
 */
public interface Command {
    
    /**
     * The name of the command.
     * @return The name of the command.
     */
    String getName();
    
    /**
     * A short description of the command.
     * @return A short description of the command.
     */
    String getDescription();
    
    /**
     * The usage of the command, such as "/command [arg1] [arg2]".
     * @return The usage of the command.
     */
    String getUsage();
    
    /**
     * The permission required to execute this command.
     * @return The permission required to execute this command.
     */
    default Permission getPermission() {
        return Permission.NONE;
    }
    
    /**
     * The message to send to the executor if they do not have permission to execute this command.
     * @return The message to send to the executor if they do not have permission to execute this command.
     */
    default String getNoPermissionMessage() {
        return "You do not have permission to do that.";
    }
    
    /**
     * Executes the command.
     * @param sender The sender of the command.
     * @param args The arguments of the command.
     * @throws CommandExecutionException If the command could not be executed.
     */
    void execute(CommandSender sender, Arguments args) throws CommandExecutionException;
    
    /**
     * Gets the tab completions for the command.
     * @param player The player who is tab completing the command.
     * @param args The in-progress arguments of the command.
     * @return The tab completions for the command's arguments.
     */
    default List<String> getTabCompletions(Player player, Arguments args) {
        return Collections.emptyList();
    }
    
    /**
     * Ensures that the command sender is a player, and returns the player.
     * @param sender The command sender.
     * @return The player.
     * @throws CommandExecutionException If the sender is not a player.
     */
    static Player playerOnly(CommandSender sender) throws CommandExecutionException {
        if (sender instanceof Player)
            return (Player) sender;
        throw new CommandExecutionException("Player command only.");
    }
    
    static void fail(String message) throws CommandExecutionException {
        throw new CommandExecutionException(message);
    }

}
