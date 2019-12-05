# Compressor Transform


Description
-----------
Compresses configured fields. Multiple fields can be specified to be compressed using different compression algorithms.
Plugin supports SNAPPY, ZIP, and GZIP types of compression of fields.


Configuration
-------------
**compressor:** Specifies the configuration for compressing fields; in JSON configuration, 
this is specified as ``<field>:<compressor>[,<field>:<compressor>]*``.

**schema:** Specifies the output schema; the fields that are compressed will have the same field name 
but they will be of type ``BYTES``.

**Note**: Do not use sink plugins that store data in textual format because Compressor converts the field values to `bytes` and text based sink plugin will convert `bytes` to `string` at the time of writing the data.
Use any columnar format like `ORC`, `Parquet` etc.

Example
-------

This example compresses the fields fname, lname and cost of a dataset using the compression format provided with the field.

```
{
    "name": "Compressor",
    "type": "transform",
    "properties": {
        "compressor": "fname:SNAPPY,lname:ZIP,cost:GZIP"
    }
}
```