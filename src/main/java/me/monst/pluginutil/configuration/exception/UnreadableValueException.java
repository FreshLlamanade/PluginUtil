package me.monst.pluginutil.configuration.exception;

/**
 * An exception that is thrown when a configuration value was fed an object which could not be made sense of.
 */
public class UnreadableValueException extends Exception {
    
    public UnreadableValueException() {
    
    }
    
    public UnreadableValueException(String message) {
        super(message);
    }

}
