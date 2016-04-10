package io.apollosoftware.naturalnavigation.configuration.mysql;

import org.bukkit.configuration.ConfigurationSection;

public abstract class DatabaseConfiguration {

    private final String uri;
    private final String user;
    private final String password;
    private final int maxConnections;


    public DatabaseConfiguration(ConfigurationSection conf) {
        uri = conf.getString("uri");
        user = conf.getString("username");
        password = conf.getString("password");
        maxConnections = 10;
    }

    public String getJDBCUrl() {
        return "jdbc:" + uri + "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10&useUnicode=true&characterEncoding=utf-8";
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

}
