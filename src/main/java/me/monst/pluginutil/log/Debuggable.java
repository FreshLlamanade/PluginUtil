package me.monst.pluginutil.log;

import java.util.logging.Logger;

public interface Debuggable {
    
    PluginLogger log();
    
    Logger getLogger();
    
    Debugger getDebugger();

}
