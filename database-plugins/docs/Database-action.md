# Database Action


Description
-----------
Action that runs a database command.


Use Case
--------
The action can be used whenever you want to run a database command before or after a data pipeline.
For example, you may want to run a sql update command on a database before the pipeline source pulls data from tables.


Properties
----------
**Plugin Name:** Name of the JDBC plugin to use. This is the value of the 'name' key
defined in the JSON file for the JDBC plugin.

**Plugin Type:** Type of the JDBC plugin to use. This is the value of the 'type' key
defined in the JSON file for the JDBC plugin. Defaults to 'jdbc'.

**Database Command:** The database command to execute.

**Connection String:** JDBC connection string including database name.

**Username:** User identity for connecting to the specified database. Required for databases that need
authentication. Optional for databases that do not require authentication.

**Password:** Password to use to connect to the specified database. Required for databases
that need authentication. Optional for databases that do not require authentication.

**Connection Arguments:** A list of arbitrary string tag/value pairs as connection arguments. These arguments
will be passed to the JDBC driver, as connection arguments, for JDBC drivers that may need additional configurations.
This is a semicolon-separated list of key-value pairs, where each pair is separated by a equals '=' and specifies
the key and value for the argument. For example, 'key1=value1;key2=value' specifies that the connection will be
given arguments 'key1' mapped to 'value1' and the argument 'key2' mapped to 'value2'. (Macro-enabled)

**Enable Auto-Commit:** Whether to enable auto-commit for queries run by this source. Defaults to 'false'.
Normally this setting does not matter.It only matters if you are using a jdbc driver 
that does not support a false value for autocommit, or a driver that throws error when auto-commit is set to false.
 For drivers like those, you will need to set this to 'true'.


Example
-------
This example connects to a database using the specified 'connectionString', which means
it will connect to the 'prod' database of a PostgreSQL instance running on 'localhost'.
It will run an update command to set the price of record with ID 6 to 20.

    {
        "name": "Database",
        "plugin": {
            "name": "Database",
            "type": "action",
            "properties": {
                "query": "UPDATE table_name SET price = 20 WHERE ID = 6",
                "connectionString": "jdbc:postgresql://localhost:5432/prod",
                "user": "user123",
                "password": "password-abc",
                "jdbcPluginName": "postgres",
                "jdbcPluginType": "jdbc"
            }
        }
    }


Notes :
-----
List of supported drivers and connection string .

    +==============================================================================================================================================+
    | DB name                      | Driver Name(class name)         |   Database URL & Example                                                    |
    +==============================================================================================================================================+
    | MySQL                        | com.mysql.jdbc.Driver           |   jdbc:mysql://<server>:<port>/<databaseName>                               |
                                                                         Eg: jdbc:mysql://localhost:3306/myDBName                                  
    | Postgre                      | org.postgresql.Driver           |   jdbc:postgresql://<server>:<port>/<databaseName>                          |
                                                                         Eg: jdbc:postgresql://localhost:5432/myDBName                  
    +==============================================================================================================================================+

Steps to upload connecter-jar for mysql using below steps :

1. copy driver jar & json at any location for ex copied in /tmp/ folder
2. goto any cdap node 
3. goto → cd /opt/cdap/master/ 
4. run command => ./bin/cdap cli -v false
5. and enter username password
6. Please, specify Username for basic authentication.> usr01
7. Please, specify Password for basic authentication.> *********
8. run command => load artifact /tmp/mysql-connector-java-x.x.x.jar config-file /tmp/mysql-connector-java-x.x.x.json name mysql-connector-java version x.x.x