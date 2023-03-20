package me.monst.pluginutil.log;

import java.util.logging.Logger;

public class SimplePluginLogger implements PluginLogger {
    
    private final Debuggable debuggable;
    private final Logger logger;
    
    public SimplePluginLogger(Debuggable debuggable) {
        this.logger = debuggable.getLogger();
        this.debuggable = debuggable;
    }
    
    @Override
    public void debug(String message) {
        debugMessage(message);
    }
    
    @Override
    public void debug(String message, Object... args) {
        debugMessage(String.format(message, args));
    }
    
    @Override
    public void debug(Throwable throwable) {
        debuggable.getDebugger().log(throwable);
    }
    
    @Override
    public void info(String message) {
        debugMessage(message);
        logger.info(message);
    }
    
    @Override
    public void info(String message, Object... args) {
        String formatted = String.format(message, args);
        debugMessage(formatted);
        logger.info(formatted);
    }
    
    @Override
    public void warn(String message) {
        debugMessage(message);
        logger.warning(message);
    }
    
    @Override
    public void warn(String message, Object... args) {
        String formatted = String.format(message, args);
        debugMessage(formatted);
        logger.warning(formatted);
    }
    
    @Override
    public void severe(String message) {
        debugMessage(message);
        logger.severe(message);
    
    }
    
    @Override
    public void severe(String message, Object... args) {
        String formatted = String.format(message, args);
        debugMessage(formatted);
        logger.severe(formatted);
    }
    
    private void debugMessage(String message) {
        debuggable.getDebugger().log(message);
    }
    
}
