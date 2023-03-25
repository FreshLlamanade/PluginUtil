package me.monst.pluginutil.log;

import org.bukkit.plugin.Plugin;

public interface Debuggable extends Plugin {
    
    PluginLogger log();

}
