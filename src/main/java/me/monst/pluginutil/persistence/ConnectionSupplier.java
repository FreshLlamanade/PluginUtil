package me.monst.pluginutil.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSupplier {
    
    Connection getConnection() throws SQLException;
    
}
