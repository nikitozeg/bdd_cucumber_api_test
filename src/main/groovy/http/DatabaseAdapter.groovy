package http

import configuration.Configuration
import groovy.sql.Sql
import groovy.util.logging.Slf4j

import java.lang.reflect.Field
import java.sql.SQLException


@Slf4j
@Singleton(strict = false)
class DatabaseAdapter {
    static Sql dbConnection

    private DatabaseAdapter() {
        def databaseConfig = Configuration.getConf().db.jdbc
        System.setProperty("java.library.path", "C:\\1");

        try {

            final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);

        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }

        def icmsDb = [
                url            : Configuration.getConf().dburl,
                username       : Configuration.getConf().dbusername,
                password       : Configuration.getConf().dbpwd,
                driverClassName: "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        ]


        try {
            dbConnection = Sql.newInstance(icmsDb.url.toString(), icmsDb.username.toString(), icmsDb.password.toString(), icmsDb.driverClassName.toString())
            log.info("--------------Connection to EPA DB established!-------------")


        }
        catch (SQLException exception) {
            exception.printStackTrace()
        }

        addShutdownHook {
            closeDbConnections()
        }
    }


    private static closeDbConnections() {
        try {
            dbConnection.close()
        }
        catch (SQLException exception) {
            exception.printStackTrace()
        }
    }

}
