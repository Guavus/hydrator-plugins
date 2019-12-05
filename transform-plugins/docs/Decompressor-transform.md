# Decompressor Transform


Description
-----------
Decompresses configured fields. Multiple fields can be specified to be decompressed using
different decompression algorithms. Plugin supports ``SNAPPY``, ``ZIP``, and ``GZIP`` types of
decompression of fields.


Configuration
-------------
**decompressor:** Specifies the configuration for decompressing fields; in JSON configuration, 
this is specified as ``<field>:<decompressor>[,<field>:<decompressor>]*``.

**Note**: Use the same format to decompress the field which was used for compression. 

**schema:** Specifies the output schema; the fields that are decompressed will have the same field 
name but they will be of type ``BYTES`` or ``STRING``.

**Note**: Do not use sink plugins that store data in textual format because Compressor converts the field values to `bytes` and text based sink plugin will convert `bytes` to `string` at the time of writing the data.
Use any columnar format like `ORC`, `Parquet` etc.

Example
-------

This example decompresses the fields fname, lname and cost of a dataset using the decompression format provided with the field.
```
{
    "name": "Field Decompressor",
    "type": "transform",
    "properties": {
        "decompressor": "fname:SNAPPY,lname:ZIP,cost:GZIP"
    }
}
```