package me.monst.pluginutil.log;

public interface Debuggable {
    
    default DebugLogger getDebugLogger() {
        return DebugLogger.NO_OP;
    }

    default void debug(String message) {
        getDebugLogger().log(message);
    }
    
    default void debug(String message, Object... args) {
        getDebugLogger().log(String.format(message, args));
    }
    
    default void debug(Throwable throwable) {
        getDebugLogger().log(throwable);
    }

}
