# Database Batch Source


Description
-----------
Database is a Guavus Enterprise Accelerator that is used to read from a database using a configurable SQL query. It outputs one record for each row returned by the query.


Use Case
--------
Consider a scenario wherein you want to create daily snapshots of a database table by using this source and writing to a TimePartitionedFileSet. This can be achieved by configuring the accelerator as described in the following section.


Properties
----------
**Reference Name:** The name used to uniquely identify this sink for lineage, annotating metadata, etc.

**Plugin Name:** The name of the JDBC plugin to use. This is the value of the 'name' key defined in the JSON file for the JDBC plugin.

**Plugin Type:** The type of JDBC plugin to use. This is the value of the 'type' key defined in the JSON file for the JDBC plugin. Defaults to 'jdbc'.

**Connection String:** The JDBC connection string including the database name. (Macro-enabled)

**Import Query:** The SELECT query to use to import data from the specified table.
You can specify an arbitrary number of columns to import, or you can import all columns using \*. The Query should
contain the '$CONDITIONS' string. For example, 'SELECT * FROM table WHERE $CONDITIONS'.
The '$CONDITIONS' string will be replaced by 'splitBy' field limits specified by the bounding query.
The '$CONDITIONS' string is not required if numSplits is set to one. (Macro-enabled)

**Bounding Query:** Bounding Query should return the min and max of the values of the 'splitBy' field. Both min and max are required in query.
For example, 'SELECT MIN(id),MAX(id) FROM table'. Not required if numSplits is set to one. (Macro-enabled)

**Split-By Field Name:** The field name which will be used to generate splits. Not required if numSplits is set to one. (Macro-enabled)

**Number of Splits to Generate:** The number of splits to generate. (Macro-enabled)

**Username:** The user identity for connecting to the specified database. Required for databases that need
authentication. Optional for databases that do not require authentication. (Macro-enabled)

**Password:** The password to use to connect to the specified database. Required for databases
that need authentication. Optional for databases that do not require authentication. (Macro-enabled)

**Connection Arguments:** A list of arbitrary string tag/value pairs as connection arguments. These arguments
will be passed to the JDBC driver, as connection arguments, for JDBC drivers that may need additional configurations.
This is a semicolon-separated list of key-value pairs, where each pair is separated by a equals '=' and specifies
the key and value for the argument. For example, 'key1=value1;key2=value' specifies that the connection will be
given arguments 'key1' mapped to 'value1' and the argument 'key2' mapped to 'value2'. (Macro-enabled)

**Enable Auto-Commit:** Whether to enable auto-commit for queries run by this source. Defaults to 'false'.
Normally this setting does not matter. It only matters if you are using a jdbc driver 
that does not support a false value for autocommit, or a driver that throws error when auto-commit is set to false.
For drivers like those, you will need to set this to 'true'.

**Column Name Case:** To set the case of the column names returned from the query. The possible options are ``upper`` or ``lower``. The default column names or the column names for any other input are not modified, and the names returned from the database are used as-is. Note that setting this property lends predictability to the column name cases across different databases, but it might result in column name conflicts if multiple columns have the same names when the case is ignored (optional).

**Transaction Isolation Level:** The transaction isolation level for queries run by this sink.
Defaults to TRANSACTION_SERIALIZABLE. See java.sql.Connection#setTransactionIsolation for more details.
The jdbc driver will throw an exception if the database does not have transactions enabled
and this setting is set to True. For drivers like that, this should be set to TRANSACTION_NONE.

**Schema:** The schema of records output by the source. This will be used in place of whatever schema is returned from the query. However, it must match the schema that returns from the query, except it can mark fields as nullable and can contain a subset of the fields.


Example
-------
This example connects to a database using the specified 'connectionString', which means it will connect to the 'prod' database of a PostgreSQL instance running on 'localhost'. It will run the 'importQuery' against the 'users' table to read four columns from the table.
The column types will be used to derive the record field types output by the source.

    {
        "name": "Database",
        "type": "batchsource",
        "properties": {
            "importQuery": "select id,name,email,phone from users where $CONDITIONS",
            "boundingQuery": "select min(id),max(id) from users",
            "splitBy": "id",
            "connectionString": "jdbc:postgresql://localhost:5432/prod",
            "user": "user123",
            "password": "password-abc",
            "jdbcPluginName": "postgres",
            "jdbcPluginType": "jdbc"
        }
    }

For example, if the 'id' column is a primary key of the type int, and the other columns are non-nullable varchars, output records will have this schema:

    +======================================+
    | field name     | type                |
    +======================================+
    | id             | int                 |
    | name           | string              |
    | email          | string              |
    | phone          | string              |
    +======================================+



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

1. Copy driver jar & json at any location for ex copied in /tmp/ folder
2. Goto any cdap node 
3. Goto → cd /opt/cdap/master/ 
4. Run command => ./bin/cdap cli -v false
5. Enter username password
6. Specify Username for basic authentication.> usr01
7. Specify Password for basic authentication.> *********
8. Run command => load artifact /tmp/mysql-connector-java-x.x.x.jar config-file /tmp/mysql-connector-java-x.x.x.json name mysql-connector-java version x.x.x
