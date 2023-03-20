package me.monst.pluginutil.log;

import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public interface Debugger {
    
    void log(String message);
    
    void log(Throwable throwable);
    
    void close();
    
    Debugger NO_OP = new Debugger() {
        @Override
        public void log(String message) {}
        
        @Override
        public void log(Throwable throwable) {}
        
        @Override
        public void close() {}
    };
    
    static Debugger printingTo(PrintWriter out) {
        return new Debugger() {
            @Override
            public void log(String message) {
                out.printf("[%s] %s%n", LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString(), message);
            }
            
            @Override
            public void log(Throwable throwable) {
                throwable.printStackTrace(out);
                out.flush();
            }
    
            @Override
            public void close() {
                out.close();
            }
        };
    }
    
}
