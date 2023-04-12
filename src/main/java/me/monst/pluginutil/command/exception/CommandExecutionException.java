package me.monst.pluginutil.command.exception;

/**
 * Thrown when a command execution fails.
 * The message of this exception is sent to the command sender.
 */
public class CommandExecutionException extends Exception {
    
    public CommandExecutionException(String message) {
        super(message);
    }
    
}
