package me.monst.pluginutil.persistence;

import org.bukkit.plugin.Plugin;

public interface Persistent extends Plugin {

    Database getDatabase();

}
