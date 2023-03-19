package me.monst.pluginutil.persistence;

@FunctionalInterface
public interface Persistent {

    Database getDatabase();

}
