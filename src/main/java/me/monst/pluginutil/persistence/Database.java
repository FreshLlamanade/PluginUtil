package me.monst.pluginutil.persistence;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface Database extends ConnectionProvider {
    
    void reload();
    
    void shutdown();

    @Nullable
    DataSource getDataSource();
    
    @Override
    default Connection getConnection() throws SQLException {
        DataSource dataSource = getDataSource();
        if (dataSource == null) {
            throw new SQLException("No data source available");
        }
        return dataSource.getConnection();
    }

}
