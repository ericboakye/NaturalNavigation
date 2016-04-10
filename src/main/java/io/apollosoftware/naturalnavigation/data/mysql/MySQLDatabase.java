package io.apollosoftware.naturalnavigation.data.mysql;

import com.j256.ormlite.db.MysqlDatabaseType;

/**
 * Class created by xenojava on 8/30/2015.
 */
public class  MySQLDatabase extends MysqlDatabaseType {
    public MySQLDatabase() {
        setCreateTableSuffix("ENGINE=MyISAM DEFAULT CHARSET=utf8");
    }
}
