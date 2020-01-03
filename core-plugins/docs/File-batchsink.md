# File Batch Sink


Description
-----------
File Batch Sink is a Guavus Enterprise Accelerator that writes to HDFS in various formats. 

For the `csv`, `delimited`, and `tsv` formats, each record is written out as delimited text. 
Complex types like arrays, maps, and records will be converted to strings using the ``toString()`` Java method. So for practical use, fields should be limited to the string, long, int, double, float, and boolean types.

For the `avro` or `parquet` formats, all types are supported.

For the `orc` format, string, long, int, double, float, boolean and array types are supported.

Use Case
-----------
Consider a scenario wherein you need to write a file to an HDFS in batch. For example, you may want to periodically dump any RDD data to HDFS in the file format like csv,tsv,json etc. To do the same, configure the File Sink accelerator as explained in the following sections.

Properties
----------
**Reference Name:** The name used to uniquely identify this sink for lineage, annotating metadata, etc.

**Path:** The path to write to. For example, /path/to/output

**Path Suffix:** The time format for the output directory that will be appended to the path.
For example, the format 'yyyy-MM-dd-HH-mm' will result in a directory of the form '2015-01-01-20-42'.
If not specified, nothing will be appended to the path."

**Format:** The format to write the records in.
The format must be one of 'json', 'avro', 'parquet', 'csv', 'tsv', 'delimited' or 'orc'.

**Delimiter:** The delimiter to use if the format is 'delimited'.

**File System Properties:** Additional properties in json format to be used with the OutputFormat when reading the data.
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
              "version": "2.1.1-SNAPSHOT_5.1.216",
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
