package me.monst.pluginutil.update;

import java.time.Duration;

public interface Download {

    int getPercentComplete();
    
    Duration getDuration();
    
    boolean isChecksumValidated();

}
