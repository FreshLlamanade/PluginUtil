package me.monst.pluginutil.log;

import java.util.logging.Logger;

public class SimplePluginLogger implements PluginLogger {
    
    private final Logger logger;
    private Debugger debugger;
    
    public SimplePluginLogger(Debuggable debuggable) {
        this.logger = debuggable.getLogger();
        this.debugger = Debugger.NO_OP;
    }
    
    @Override
    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }
    
    public Debugger getDebugger() {
        return debugger;
    }
    
    @Override
    public void debug(String message) {
        debugger.debug(message);
    }
    
    @Override
    public void debug(String message, Object... args) {
        debugger.debug(String.format(message, args));
    }
    
    @Override
    public void debug(Throwable throwable) {
        debugger.debug(throwable);
    }
    
    @Override
    public void info(String message) {
        debugger.debug(message);
        logger.info(message);
    }
    
    @Override
    public void info(String message, Object... args) {
        String formatted = String.format(message, args);
        debugger.debug(formatted);
        logger.info(formatted);
    }
    
    @Override
    public void warn(String message) {
        debugger.debug(message);
        logger.warning(message);
    }
    
    @Override
    public void warn(String message, Object... args) {
        String formatted = String.format(message, args);
        debugger.debug(formatted);
        logger.warning(formatted);
    }
    
    @Override
    public void severe(String message) {
        debugger.debug(message);
        logger.severe(message);
    
    }
    
    @Override
    public void severe(String message, Object... args) {
        String formatted = String.format(message, args);
        debugger.debug(formatted);
        logger.severe(formatted);
    }
    
}
