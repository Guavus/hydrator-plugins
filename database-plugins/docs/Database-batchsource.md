# Database


## Description
The Database open source accelerator is used to read from a database using a configurable SQL query. After reading data from the database as per the query, the accelerator produces an output record for each row.


## Use Case
Consider a scenario wherein you want to read data from a database by using this source and then write to a TimePartitionedFileSet. This can be achieved by configuring the accelerator as described in the following section.


## Properties

The following pointers describe the fields as displayed in the accelerator properties dialog box.

**Reference Name:** Enter a name for this sink to uniquely identify it for lineage, annotating metadata, and so on.

**Plugin Name:** Enter the name of the JDBC driver. This refers to the 'name' key defined in the JSON file that was used at the time of uploading the JDBC driver. For more information on how to upload a driver, refer the section 'Upload Database Driver'.

**Plugin Type:** Specify the type of JDBC plugin to be used. This is the value of the 'type' key defined in the JSON file for the JDBC plugin. The default type is 'jdbc'.

**Connection String:** Specify the JDBC connection string, including the database name. This field is macro-enabled.

**Import Query:** Enter the SELECT query to be used to import data from the specified table. You can specify an arbitrary number of columns to import, or you can import all columns using \*. The Query should contain the $CONDITIONS string. For example, 'SELECT * FROM table WHERE $CONDITIONS'.
The $CONDITIONS string will be replaced by 'splitBy' field limits specified by the bounding query.
The $CONDITIONS string is not required if numSplits is set to one. This field is macro-enabled.

**Bounding Query:** A bounding query is required when split is set to a value more than one. Enter the Bounding Query that should return the min and max of the values of the 'splitBy' field. Both min and max are required in the query; for example, 'SELECT MIN(id),MAX(id) FROM table'. This is not required if numSplits is set to one. (Macro-enabled)

**Split-By Field Name:** Enter the field name that will be used to generate splits. This is not required if numSplits is set to one. This field is macro-enabled.

**Number of Splits to Generate:** Specify the number of splits to be generated. This field is macro-enabled.

**Username:** Enter the user name for connecting to the specified database. It is mandatory for databases that require authentication but optional for databases that do not require authentication. This field is macro-enabled.

**Password:** Enter the password to be used to connect to the specified database. It is mandatory for databases that require authentication but optional for databases that do not require authentication. This field is macro-enabled.

**Connection Arguments:** Enter a list of arbitrary string tag/value pairs as connection arguments. These arguments are passed to the JDBC driver as connection arguments if additional configurations are needed.
This should be a semicolon-separated list of key-value pairs, where values in each pair are separated by an equals '=' sign and a pair specifies the key and value for the argument. For example, 'key1=value1;key2=value' specifies that the connection will be given arguments 'key1' mapped to 'value1' and the argument 'key2' mapped to 'value2'. This field is macro-enabled.

**Enable Auto-Commit:** Choose 'True' or 'False' based on whether you want to enable auto-commit for queries run by this source. By default, 'False' is selected.
**Note** This option is important only when you are using a jdbc driver that does not support a false value for autocommit, or a driver that throws an error when auto-commit is set to false. For such drivers, you will need to set this to 'true'.

**Column Name Case:** Select a case for the column names returned from the query. The available options are ``upper`` and ``lower``. The default column names or the column names for any other input are not modified, and the names returned from the database are used as-is. Note that setting this property lends a predictability to the column name cases across different databases, but it might result in column name conflicts if multiple columns have the same names when the case is ignored.

**Transaction Isolation Level:** From the drop-down list, select the transaction isolation level for queries run by this accelerator. By default, TRANSACTION_SERIALIZABLE is selected. See Java documentation of class java.sql.Connection#setTransactionIsolation for more details. 
The JDBC driver will throw an exception if the database does not have transactions enabled and this setting is set to True. For such drivers, this property should be set to TRANSACTION_NONE.

**Schema:** Specify the schema of records that the source outputs. This is used in place of the schema that is returned from the query. It must match that schema except that it can mark fields as nullable and can contain a subset of the fields available in the result.


## Example
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

For example, if the 'id' column is a primary key of the type int and the other columns are non-nullable varchars, the output records will have this schema:

    +======================================+
    | field name     | type                |
    +======================================+
    | id             | int                 |
    | name           | string              |
    | email          | string              |
    | phone          | string              |
    +======================================+



## Notes :

1. `The following table lists the supported drivers and their connection details.`

```
+=====================================================================================================+
| DB name  | Driver Name(class name)          |   Database URL & Example                              |
+=====================================================================================================+
| MySQL    | com.mysql.jdbc.Driver            |   jdbc:mysql://<server>:<port>/<databaseName>         |
                                                  Eg: jdbc:mysql://localhost:3306/myDBName

| Postgres | org.postgresql.Driver            |   jdbc:postgresql://<server>:<port>/<databaseName>    |
                                                  Eg: jdbc:postgresql://localhost:5432/myDBName

| H2DB     | org.h2.Driver                    |   jdbc:h2:tcp://<server>:<port>/<databasePath>        |
                                                  Eg: jdbc:h2:tcp://192.168.111.139:9092/~/test

| Oracle   | oracle.jdbc.driver.OracleDriver  |   jdbc:oracle:thin:@<host>:<port>/<serviceName>       |
                                                  Eg: jdbc:oracle:thin:@localhost:1521/orcls
+=====================================================================================================+
```

2. Transaction Isolation Level is supported in MySQL and Postgres dbs:

***MySql/Postgres*** :  "TRANSACTION_READ_UNCOMMITTED", "TRANSACTION_READ_COMMITTED","TRANSACTION_REPEATABLE_READ",
                        "TRANSACTION_SERIALIZABLE (default)" .

### Upload Database Driver

To use this accelerator to connect to supported databases, upload the corresponding driver in CDAP.

The corresponding driver jar can be downloaded from the internet. The following table lists the tested driver versions:

```
+===========================+
| DB name  | Driver Version |
+===========================+
| MySQL    | 8.0.18         |
| Postgres | 9.4.1211       |
| H2DB     | 1.4.200        |
| Oracle   | 12.1.0.2       |
+===========================+
```

* Copy the driver jar at any location on one of the cdap master nodes. For example, copy `h2-1.4.200.jar` in `/tmp` folder.
* Create a json file with the following content and copy the file in the same directory used in the step above. The name of the json file should be the same as the jar file with extension `.json`. For example, `h2-1.4.200.json`
```
{
 "plugins": [
    {
      "name": "<Driver Name>",
      "type": "jdbc",
      "description": <Driver description>",
      "className": "<Driver Class>"
    }
  ]
}
```

**Example:** 

For the h2db content of json file

```
{
 "plugins": [
    {
      "name": "h2db",
      "type": "jdbc",
      "description": "H2DB JDBC external plugin",
      "className": "org.h2.Driver"
    }
  ]
}
```
* Log into one of the cdap master nodes
* Navigate to the directory `/opt/cdap/master`
* Run the command `./bin/cdap cli -v false`
* Enter the username and password when prompted
* Run the following command to load driver:
`load artifact <driver-jar-path> config-file <json-path> name <connector-name> version <driver-version>`
<br/> **For example:** 
`load artifact /tmp/h2-1.4.200.jar config-file /tmp/h2-1.4.200.json name h2db-connector-java version 1.4.200`

* Use the following Rest API to verify if the driver is uploaded successfully:<br/>
`namespaces/default/artifacts/h2db-connector-java/versions/1.4.200`
<br/> 
* The expected output is as follows:

```
{
  "classes": {
    "apps": [],
    "plugins": [
      {
        "type": "jdbc",
        "name": "h2db",
        "description": "H2DB JDBC external plugin",
        "className": "org.h2.Driver",
        "properties": {},
        "endpoints": [],
        "requirements": {
          "datasetTypes": []
        }
      }
    ]
  },
  "properties": {},
  "parents": [],
  "name": "h2db-connector-java",
  "version": "1.4.200",
  "scope": "USER"
}
```

