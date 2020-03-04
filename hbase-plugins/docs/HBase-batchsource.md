# HBase



Description
-----------

HBase is a Guavus Enterprise Accelerator that can be used when you want to read directly from a column family in an HBase table, without using a CDAP dataset

Use Case
--------
Consider a scenario wherein you want to read from an HBase table, filter some records out, and then write the results to a Database table. It can be achieved my making configurational changes as described in the sections below.


Properties
----------
**referenceName:** This will be used to uniquely identify this source for lineage, annotating metadata, etc.

**tableName:** The name of the table to read from. (Macro-enabled)

**columnFamily:** The name of the column family to read from. **Note:** Only single column family is supported.(Macro-enabled)

**schema:** The Schema of records read from the table. For example, if the schema contains a field named 'user' of the type string, the value of that field will be taken from the value stored in the 'user' column. Only the simple types are allowed (boolean, int, long, float, double, bytes, string).

**rowField:** The field name indicating that the field value should
come from the row key instead of a row column. The field name specified must be present in
the schema, and must not be nullable. Only single row key field is supported.

**zkQuorum:** The ZooKeeper quorum for the hbase instance you are reading from. This should be a comma separated list of hosts that make up the quorum. You can find the correct value by looking at the ``hbase.zookeeper.quorum`` setting in your ``hbase-site.xml`` file (Macro-enabled).

**zkClientPort:** The client port used to connect to the ZooKeeper quorum. You can find the correct value by looking at the ``hbase.zookeeper.property.clientPort`` setting in your ``hbase-site.xml``.
This value defaults to ``2181``. (Macro-enabled)


Example
-------
This example reads from the 'attr' column family of an HBase table named 'users':

    {
        "name": "HBase",
        "type": "batchsource",
        "properties": {
            "tableName": "users",
            "columnFamily": "attr",
            "rowField": "id",
            "zkQuorum": "host1,host2,host3",
            "zkClientPort": "2181",
            "schema": "{
                \"type\":\"record\",
                \"name\":\"user\",
                \"fields\":[
                    {\"name\":\"id\",\"type\":\"long\"},
                    {\"name\":\"name\",\"type\":\"string\"},
                    {\"name\":\"birthyear\",\"type\":\"int\"}
                ]
            }",
            "schema.row.field": "id"
        }
    }

It outputs records with this schema:

    +======================================+
    | field name     | type                |
    +======================================+
    | id             | long                |
    | name           | string              |
    | birthyear      | int                 |
    +======================================+

The 'id' field will be read from the row key of the table. The 'name' field will be read from the 'name' column in the table. The 'birthyear' field will be read from the 'birthyear' column in the table. Any other columns in the table will be ignored by the source.
