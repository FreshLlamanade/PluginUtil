package me.monst.pluginutil.configuration.exception;

/**
 * An exception that is thrown when a user-entered string could not be parsed.
 */
public class ArgumentParseException extends UnreadableValueException {
    
    public ArgumentParseException(String message) {
        super(message);
    }
    
}
