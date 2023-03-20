package me.monst.pluginutil.log;

public interface PluginLogger {
    
    void debug(String message);
    
    void debug(String message, Object... args);
    
    void debug(Throwable throwable);

    void info(String message);
    
    void info(String message, Object... args);
    
    void warn(String message);
    
    void warn(String message, Object... args);
    
    void severe(String message);
    
    void severe(String message, Object... args);
    
    static PluginLogger of(Debuggable debuggable) {
        return new SimplePluginLogger(debuggable);
    }

}
