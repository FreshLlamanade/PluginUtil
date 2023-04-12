package me.monst.pluginutil.persistence;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionProvider {
    
    Connection getConnection() throws SQLException;
    
}
