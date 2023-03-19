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
    default void execute(CommandSender sender, Args args) throws CommandException {
        Executable subCommand;
        if (args.isEmpty() || (subCommand = getSubCommands().get(args.first())) == null) {
            getSubCommands().values().stream()
                    .filter(cmd -> cmd.getPermission().ownedBy(sender))
                    .map(this::displaySubCommand)
                    .forEach(sender::sendMessage);
            return;
        }
        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            throw new CommandException("Player command only.");
        }
        if (subCommand.getPermission().notOwnedBy(sender)) {
            throw new CommandException(subCommand.getNoPermissionMessage());
        }
        subCommand.execute(sender, args.descend());
    }
    
    @Override
    default List<String> getTabCompletions(Player player, Args args) {
        if (args.isEmpty() || getPermission().notOwnedBy(player)) {
            return Collections.emptyList();
        }
        if (args.size() == 1) {
            return getSubCommands().keySet().stream()
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, args.first()))
                    .collect(Collectors.toList());
        }
        
        Executable subCommand = getSubCommands().get(args.first());
        if (subCommand == null) {
            return Collections.emptyList();
        }
        return subCommand.getTabCompletions(player, args.descend());
    }
    
    default String displaySubCommand(Executable subCommand) {
        return new ColorStringBuilder()
                .green().bold(subCommand.getUsage())
                .gold(" : ")
                .darkGreen(subCommand.getDescription())
                .toString();
    }
    
}
