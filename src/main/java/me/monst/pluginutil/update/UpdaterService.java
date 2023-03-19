package me.monst.pluginutil.update;

public interface UpdaterService {
    
    /**
     * Checks for an update on GitHub.
     * @return A promise which will resolve to the latest update if one is available.
     */
    Promise<Update> checkForUpdate();
    
    /**
     * Returns the latest update if one has already been found.
     * @return The latest update if one is available, or null if no update has been found yet.
     */
    Update getUpdateIfAvailable();
    
}
