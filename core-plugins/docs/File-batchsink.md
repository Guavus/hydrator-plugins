# File Sink


Description
-----------
Writes to a filesystem in various formats format.

For the csv, delimited, and tsv formats, each record is written out as delimited text.
Complex types like arrays, maps, and records will be converted to strings using their
``toString()`` Java method, so for practical use, fields should be limited to the
string, long, int, double, float, and boolean types.

All types are supported when using the avro or parquet format.

Properties
----------
**Reference Name:** Name used to uniquely identify this sink for lineage, annotating metadata, etc.

**Path:** Path to write to. For example, /path/to/output

**Path Suffix:** Time format for the output directory that will be appended to the path.
For example, the format 'yyyy-MM-dd-HH-mm' will result in a directory of the form '2015-01-01-20-42'.
If not specified, nothing will be appended to the path."

**Format:** Format to write the records in.
The format must be one of 'json', 'avro', 'parquet', 'csv', 'tsv', or 'delimited'.

**Delimiter:** Delimiter to use if the format is 'delimited'.

**File System Properties:** Additional properties to use with the OutputFormat when reading the data.

Advanced features can be used to specify any additional property that should be used with the sink.

## Configuration

| Configuration | Label | Required? | Default | Description |
| :------------ | :---- | :-------- | :------ | :---------- |
| `Reference Name` | label | Yes | File | Name used to uniquely identify this sink for lineage, annotating metadata, etc.|
| `Path` | Path | Yes | N/A | Path to write to. For example, /path/to/output. |
| `Path Suffix` | Path Suffix | Optional | yyyy-MM-dd-HH-mm | Time format for the output directory that will be appended to the path.For example, the format 'yyyy-MM-dd-HH-mm' will result in a directory of the form '2015-01-01-20-42'.If not specified, nothing will be appended to the path. |
| `Format` | Format | Yes | Json | Format to write the records in. The format must be one of 'json', 'avro', 'parquet', 'csv', 'tsv', 'delimited' or 'orc'.|
| `Delimiter` | Delimiter | Optional | N/A | Delimiter to use if the format is 'delimited'.|
| `File System Properties` | File System Properties | Optional | N/A | Additional properties in json format to use with the OutputFormat when reading the data.Advanced feature to specify any additional property that should be used with the sink. See [here](#file-system-properties) for details.|


### File System Properties
This is a JSON string representing a map of properties that can can be used when writing the data depending on the use case.

Here are sample use cases

- ##### Setting up stripe size when writing to Orc format.
```json
{
  "orc.stripe.size": "67108864"
}
``` 
- ##### Writing output to gzip compression format
```json
{
    "mapreduce.output.fileoutputformat.compress": "true",
    "mapreduce.output.fileoutputformat.compress.codec": "org.apache.hadoop.io.compress.GzipCodec"
}
```

- ##### Writing output to bzip2 compression format
```json
{
   "mapreduce.output.fileoutputformat.compress": "true",
   "mapreduce.output.fileoutputformat.compress.codec": "org.apache.hadoop.io.compress.BZip2Codec"
 }
```

## Sample Pipeline

    {
          "name": "File",
          "plugin": {
            "name": "File",
            "type": "batchsink",
            "label": "File",
            "artifact": {
              "name": "core-plugins",
              "version": "2.1.1-SNAPSHOT",
              "scope": "SYSTEM"
            },
            "properties": {
              "referenceName": "ref_hdfs_sink",
              "suffix": "yyyy-MM-dd-HH-mm",
              "format": "json",
              "path": "/tmp/outputFile",
              "delimiter": ",",
              "fileSystemProperties": "{\"mapreduce.output.fileoutputformat.compress\":\"true\",\"mapreduce.output.fileoutputformat.compress.codec\":\"org.apache.hadoop.io.compress.GzipCodec\"}"
            }
          }
    }