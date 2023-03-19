package me.monst.pluginutil.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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
    
    public void register(TopLevelExecutable... commands) {
        for (TopLevelExecutable command : commands) {
            register(command);
        }
    }
    
    public void register(TopLevelExecutable command) {
        commandMap.register(plugin.getName(), toPluginCommand(command));
    }
    
    private PluginCommand toPluginCommand(TopLevelExecutable command) {
        try {
            PluginCommand pluginCommand = constructor.newInstance(command.getName(), plugin);
            pluginCommand.setDescription(command.getDescription());
            pluginCommand.setUsage(command.getUsage());
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
            return pluginCommand;
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().severe("Failed to create plugin command for command '" + command.getName() + "'");
            throw new RuntimeException(e);
        }
    }
    
}
