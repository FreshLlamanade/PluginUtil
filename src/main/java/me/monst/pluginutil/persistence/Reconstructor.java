package me.monst.pluginutil.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface Reconstructor<T> {
    
    T reconstruct(ResultSet resultSet, Connection con) throws SQLException;
    
}
