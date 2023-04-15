package me.monst.pluginutil.command;

import me.monst.pluginutil.command.exception.CommandExecutionException;
import org.bukkit.command.CommandSender;

public class SimpleCommand implements Command {
    
    private final String name;
    private final String description;
    private final String usage;
    private final Executor executor;
    
    public SimpleCommand(String name, String description, String usage, Executor executor) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.executor = executor;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getUsage() {
        return usage;
    }
    
    protected interface Executor {
        void execute(CommandSender sender, Arguments args) throws CommandExecutionException;
    }
    
    @Override
    public void execute(CommandSender sender, Arguments args) throws CommandExecutionException {
        executor.execute(sender, args);
    }
    
}
