package me.monst.pluginutil.command;

import me.monst.pluginutil.command.exception.CommandExecutionException;
import me.monst.pluginutil.command.exception.NoPermissionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandRegisterService {
    
    private final Plugin plugin;
    private Constructor<PluginCommand> constructor;
    private CommandMap commandMap;
    
    public CommandRegisterService(Plugin plugin) {
        this.plugin = plugin;
        initReflection();
    }
    
    private void initReflection() {
        try {
            constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getPluginManager());
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().severe("Failed to initialize command register service!");
            throw new RuntimeException(e);
        }
    }
    
    public void register(Command... commands) {
        for (Command command : commands) {
            register(command);
        }
    }
    
    public void register(Command command) {
        commandMap.register(plugin.getName(), toPluginCommand(command));
    }
    
    private PluginCommand toPluginCommand(Command command) {
        try {
            PluginCommand pluginCommand = constructor.newInstance(command.getName(), plugin);
            pluginCommand.setDescription(command.getDescription());
            pluginCommand.setUsage(command.getUsage());
            pluginCommand.setExecutor(toCommandExecutor(command));
            pluginCommand.setTabCompleter(toTabCompleter(command));
            return pluginCommand;
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().severe("Failed to create plugin command for command '" + command.getName() + "'");
            throw new RuntimeException(e);
        }
    }
    
    private CommandExecutor toCommandExecutor(Command command) {
        return (sender, cmd, label, args) -> {
            if (command.getPermission().notOwnedBy(sender)) {
                sender.sendMessage(ChatColor.DARK_RED + command.getNoPermissionMessage());
                return true;
            }
            try {
                command.execute(sender, new ArgumentsImpl(args));
            } catch (NoPermissionException e) {
                sender.sendMessage(ChatColor.DARK_RED + e.getMessage());
            } catch (CommandExecutionException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
            return true;
        };
    }
    
    private TabCompleter toTabCompleter(Command command) {
        return (sender, cmd, alias, args) -> {
            if (args.length == 0 || !(sender instanceof Player) || command.getPermission().notOwnedBy(sender))
                return Collections.emptyList();
            Iterable<?> iterable = command.getTabCompletions((Player) sender, new ArgumentsImpl(args));
            if (iterable == null)
                return Collections.emptyList();
            List<String> tabCompletions = new ArrayList<>();
            for (Object object : iterable)
                tabCompletions.add(String.valueOf(object));
            return tabCompletions;
        };
    }
    
}
