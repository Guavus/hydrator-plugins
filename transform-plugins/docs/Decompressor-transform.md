# Decompressor Transform


Description
-----------
Decompresses configured fields. Multiple fields can be specified to be decompressed using
different decompression algorithms. Plugin supports ``SNAPPY``, ``ZIP``, and ``GZIP`` types of
decompression of fields.


Configuration
-------------
**decompressor:** List of key value pairs. Key represents the input field that needs to be decompressed and Value represents the decompression algorithm to use.

**Note**: Use the same format to decompress the field which was used for compression. 

**schema:** Specifies the output schema; the fields that are decompressed will have the same field 
name but they will be of type ``BYTES`` or ``STRING``.

**Note**: 
- For input only use columnar datasets like `ORC`, `Parquet` etc.
- Input fields that need to decompressed must be of type `Bytes` and non-nullable.

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

Input
```
+======================================================================+
|        fname       |        lname       |         cost       |zipcode|
+======================================================================+
|[03 08 62 6F 62]    |[50 4B 03 04 14 0...|[1F 8B 08 00 00 0...| 12345 |
|[03 08 62 6F 62]    |[50 4B 03 04 14 0...|[1F 8B 08 00 00 0...| 23456 |
|[05 10 61 6C 69 6...|[50 4B 03 04 14 0...|[1F 8B 08 00 00 0...| 34567 |
|[03 08 62 6F 62]    |[50 4B 03 04 14 0...|[1F 8B 08 00 00 0...| 45678 |
|[05 10 61 6C 69 6...|[50 4B 03 04 14 0...|[1F 8B 08 00 00 0...| 56789 |
|[05 10 61 6C 69 6...|[50 4B 03 04 14 0...|[1F 8B 08 00 00 0...| 67890 |
+======================================================================+
```

Output

    +======================================+
    | fname  | lname   | cost   |  zipcode |
    +======================================+
    | bob    | smith   | 50.23  |  12345   |
    | bob    | jones   | 30.64  |  23456   |
    | alice  | smith   | 1.50   |  34567   |
    | bob    | smith   | 0.50   |  45678   |
    | alice  | smith   | 30.21  |  56789   |
    | alice  | jones   | 500.93 |  67890   |
    +======================================+