package me.monst.pluginutil.command.exception;

/**
 * Thrown when a command sender does not have permission to execute a command.
 * The message of this exception is sent to the command sender.
 */
public class NoPermissionException extends CommandExecutionException {
    
    public NoPermissionException(String message) {
        super(message);
    }
    
}
