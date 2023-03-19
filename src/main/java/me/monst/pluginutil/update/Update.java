package me.monst.pluginutil.update;

public interface Update {
    
    Download download();
    
    Download getDownload();
    
    void pauseDownload();
    
    boolean isDownloaded();
    
    UpdateState getState();
    
    String getVersion();
    
    String getReleaseNotes();
    
    long getFileSizeBytes();
    
}
