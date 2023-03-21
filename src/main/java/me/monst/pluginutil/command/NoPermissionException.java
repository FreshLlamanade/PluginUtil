package me.monst.pluginutil.command;

public class NoPermissionException extends CommandExecutionException {
    
    public NoPermissionException(String message) {
        super(message);
    }
    
}
