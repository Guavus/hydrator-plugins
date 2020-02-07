# File Sink


## Description

File Batch Sink is a Accelerator that writes to HDFS in various formats.

For the csv, delimited, and tsv formats, each record is written out as delimited text.
Complex types like arrays, maps, and records will be converted to strings using their
``toString()`` Java method, so for practical use, fields should be limited to the
string, long, int, double, float, and boolean types.

All types are supported when using the avro or parquet format.

## Use Case

Consider a scenario wherein you need to write a file to an HDFS in batch. For example, you may want to periodically dump any RDD data to HDFS in the file format like csv,tsv,json etc. To do the same, configure the File Sink accelerator as explained in the following sections.

## Configuration

**Reference Name:** Name used to uniquely identify this sink for lineage, annotating metadata, etc.

**Path:** Path to write to. For example, /path/to/output.

**Path Suffix:** Time format for the output directory that will be appended to the path. For example, the format 'yyyy-MM-dd-HH-mm' will result in a directory of the form '2015-01-01-20-42'.If not specified, nothing will be appended to the path.

**Format:** Format to write the records in. The format must be one of 'json', 'avro', 'parquet', 'csv', 'tsv', or 'delimited'.

**Delimiter:** Delimiter to use if the format is 'delimited'.

**File System Properties:** Additional properties in json format to use with the OutputFormat when reading the data. 

Advanced feature to specify any additional property that should be used with the sink. See [here](#file-system-properties) for details.

### File System Properties
This is a JSON string representing a map of properties that can can be used when writing the data depending on the use case.

Here are sample use cases

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
