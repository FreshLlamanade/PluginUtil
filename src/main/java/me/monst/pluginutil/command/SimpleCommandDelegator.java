package me.monst.pluginutil.command;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleCommandDelegator implements CommandDelegator {
    
    private final String name;
    private final String description;
    private final Map<String, Command> subCommands = new LinkedHashMap<>();
    
    public SimpleCommandDelegator(String name, String description, Command... subCommands) {
        this.name = name;
        this.description = description;
        for (Command subCommand : subCommands) {
            addSubCommand(subCommand);
        }
    }
    
    protected void addSubCommand(Command subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
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
    public Map<String, Command> getSubCommands() {
        return subCommands;
    }
    
}
