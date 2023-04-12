package me.monst.pluginutil.command;

import me.monst.pluginutil.command.exception.CommandExecutionException;
import me.monst.pluginutil.command.exception.NoPermissionException;
import me.monst.pluginutil.lang.ColorStringBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A command which delegates to different sub-commands based on its command arguments.
 */
public interface CommandDelegator extends Command {
    
    Map<String, Command> getSubCommands();
    
    @Override
    default void execute(CommandSender sender, Arguments args) throws CommandExecutionException {
        Command subCommand = args.first()
                .map(input -> getSubCommands().get(input))
                .expect(
                        () -> getSubCommands().values().stream()
                                .filter(cmd -> cmd.getPermission().ownedBy(sender))
                                .map(this::displaySubCommand)
                                .collect(Collectors.joining("\n"))
                );
        if (subCommand.getPermission().notOwnedBy(sender)) {
            throw new NoPermissionException(subCommand.getNoPermissionMessage());
        }
        subCommand.execute(sender, args.from(1));
    }
    
    @Override
    default Iterable<?> getTabCompletions(Player player, Arguments args) {
        if (args.isEmpty())
            return Collections.emptyList();
        if (args.size() == 1) {
            String arg = args.first().orElse("");
            return getSubCommands().entrySet().stream()
                    .filter(entry -> entry.getValue().getPermission().ownedBy(player))
                    .map(Map.Entry::getKey)
                    .filter(name -> StringUtil.startsWithIgnoreCase(name, arg))
                    .collect(Collectors.toList());
        }
        return args.first()
                .map(input -> getSubCommands().get(input))
                .map(cmd -> cmd.getTabCompletions(player, args.from(1)))
                .orElse(Collections.emptyList());
    }
    
    default String displaySubCommand(Command subCommand) {
        return new ColorStringBuilder()
                .green(subCommand.getUsage())
                .gold(": ")
                .darkGreen(subCommand.getDescription())
                .toString();
    }
    
}
