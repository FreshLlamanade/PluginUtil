package me.monst.pluginutil.update;

import org.bukkit.plugin.Plugin;

public interface AutoUpdateable extends Plugin {

    UpdaterService getUpdaterService();

}
