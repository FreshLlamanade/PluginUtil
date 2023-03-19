package me.monst.pluginutil.update;

public enum UpdateState {
    /** The update has not begun downloading yet.*/
    INITIAL,
    
    /** The update is currently being downloaded.*/
    DOWNLOADING,
    
    /** The download process was paused.*/
    PAUSED,
    
    /** The download process has completed and the file is being validated.*/
    VALIDATING,
    
    /** The file has been successfully downloaded and validated.*/
    SUCCESS,
    
    /** The download failed and may be retried.*/
    DOWNLOAD_FAILED,
    
    /** The downloaded file was corrupted and must be downloaded again.*/
    VALIDATION_FAILED
}
