# Database Batch Sink


Description
-----------
Writes records to a database table. Each record will be written to a row in the table.


Use Case
--------
This sink is used whenever you need to write to a database table.
Suppose you periodically build a recommendation model for products on your online store.
The model is stored in a FileSet and you want to export the contents
of the FileSet to a database table where it can be served to your users.


Properties
----------
**Reference Name:** Name used to uniquely identify this sink for lineage, annotating metadata, etc.

**Plugin Name:** Name of the JDBC plugin to use. This is the value of the 'name' key
defined in the JSON file for the JDBC plugin.

**Plugin Type:** Type of the JDBC plugin to use. This is the value of the 'type' key
defined in the JSON file for the JDBC plugin. Defaults to 'jdbc'.

**Connection String:** JDBC connection string including database name. (Macro-enabled)

**Table Name:** Name of the table to export to. (Macro-enabled)

**Columns:** Comma-separated list of columns in the specified table to export to.

**Username:** User identity for connecting to the specified database. Required for databases that need
authentication. Optional for databases that do not require authentication. (Macro-enabled)

**Password:** Password to use to connect to the specified database. Required for databases
that need authentication. Optional for databases that do not require authentication. (Macro-enabled)

**Connection Arguments:** A list of arbitrary string tag/value pairs as connection arguments. These arguments
will be passed to the JDBC driver, as connection arguments, for JDBC drivers that may need additional configurations.
This is a semicolon-separated list of key-value pairs, where each pair is separated by a equals '=' and specifies
the key and value for the argument. For example, 'key1=value1;key2=value' specifies that the connection will be
given arguments 'key1' mapped to 'value1' and the argument 'key2' mapped to 'value2'. (Macro-enabled)

**Enable Auto-Commit:** Whether to enable auto-commit for queries run by this sink. Defaults to 'false'.
Normally this setting does not matter. It only matters if you are using a jdbc driver 
that does not support a false value for autocommit, or a driver that throws error when auto-commit is set to false.
For drivers like those, you will need to set this to 'true'.

**Column Name Case:** Sets the case of the column names returned by the column check query.
Possible options are ``upper`` or ``lower``. By default or for any other input, the column names are not modified and
the names returned from the database are used as-is. Note that setting this property provides predictability
of column name cases across different databases but might result in column name conflicts if multiple column
names are the same when the case is ignored (optional).

**Transaction Isolation Level:** The transaction isolation level for queries run by this sink.
Defaults to TRANSACTION_SERIALIZABLE. See java.sql.Connection#setTransactionIsolation for more details.
The jdbc driver will throw an exception if the database does not have transactions enabled
and this setting is set to true. For drivers like that, this should be set to TRANSACTION_NONE.

Example
-------
This example connects to a database using the specified 'connectionString', which means
it will connect to the 'prod' database of a PostgreSQL instance running on 'localhost'.
Each input record will be written to a row of the 'users' table, with the value for each
column taken from the value of the field in the record. For example, the 'id' field in
the record will be written to the 'id' column of that row.

    {
        "name": "Database",
        "type": "batchsink",
        "properties": {
            "tableName": "users",
            "columns": "id,name,email,phone",
            "connectionString": "jdbc:postgresql://localhost:5432/prod",
            "user": "postgres",
            "password": "",
            "jdbcPluginName": "postgres",
            "jdbcPluginType": "jdbc"
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

Transaction Isolation Level supports for listed dbs:

***MySql/Postgres*** :  "TRANSACTION_READ_UNCOMMITTED", "TRANSACTION_READ_COMMITTED","TRANSACTION_REPEATABLE_READ",
                        "TRANSACTION_SERIALIZABLE (default)" .


Steps to upload connecter-jar for mysql using below steps :

1. copy driver jar & json at any location for ex copied in /tmp/ folder
2. goto any cdap node 
3. goto → cd /opt/cdap/master/ 
4. run command => ./bin/cdap cli -v false
5. and enter username password
6. Please, specify Username for basic authentication.> usr01
7. Please, specify Password for basic authentication.> *********
8. run command => load artifact /tmp/mysql-connector-java-x.x.x.jar config-file /tmp/mysql-connector-java-x.x.x.json name mysql-connector-java version x.x.x