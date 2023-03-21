package me.monst.pluginutil.command;

import me.monst.pluginutil.lang.ColorStringBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A command which serves only as a delegator to its various sub-commands.
 */
public interface Delegator extends Executable {
    
    Map<String, Executable> getSubCommands();
    
    @Override
    default void execute(CommandSender sender, List<String> args) throws CommandExecutionException {
        Executable subCommand;
        if (args.isEmpty() || (subCommand = getSubCommands().get(args.get(0))) == null) {
            getSubCommands().values().stream()
                    .filter(cmd -> cmd.getPermission().ownedBy(sender))
                    .map(this::displaySubCommand)
                    .forEach(sender::sendMessage);
            return;
        }
        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            throw new CommandExecutionException("Player command only.");
        }
        if (subCommand.getPermission().notOwnedBy(sender)) {
            throw new NoPermissionException(subCommand.getNoPermissionMessage());
        }
        subCommand.execute(sender, args.subList(1, args.size()));
    }
    
    @Override
    default List<String> getTabCompletions(Player player, List<String> args) {
        if (args.isEmpty()) {
            return Collections.emptyList();
        }
        if (args.size() == 1) {
            return getSubCommands().entrySet().stream()
                    .filter(entry -> entry.getValue().getPermission().ownedBy(player))
                    .map(Map.Entry::getKey)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, args.get(0)))
                    .collect(Collectors.toList());
        }
        
        Executable subCommand = getSubCommands().get(args.get(0));
        if (subCommand == null) {
            return Collections.emptyList();
        }
        return subCommand.getTabCompletions(player, args.subList(1, args.size()));
    }
    
    default String displaySubCommand(Executable subCommand) {
        return new ColorStringBuilder()
                .green(subCommand.getUsage())
                .gold(": ")
                .darkGreen(subCommand.getDescription())
                .toString();
    }
    
}
