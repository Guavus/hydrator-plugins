# File Sink


## Description

File Batch Sink is a plugin used to write to HDFS in various formats.

For the csv, delimited, and tsv formats, each record is written out as delimited text.
Complex types like arrays, maps, and records is converted to strings using their
``toString()`` Java method, so for practical use, fields should be limited to the
string, long, int, double, float, and boolean types.

All types are supported when using the avro or parquet format.

## Use Case

A scenario where you want to write a file to a HDFS in batch. For example, you may want to periodically dump any RDD data to HDFS in the file format like csv, tsv, json etc. To do this, configure the File Sink accelerator as explained in the following sections:

## Configuration

**Reference Name:** This is a unique name used to identify this source for lineage, annotating metadata, so on.

**Path:** The path to read from. For example, /path/to/output.

**Path Suffix:** Add time format for the output directory to the path. For example, the format 'yyyy-MM-dd-HH-mm' will result in a directory of the form of '2015-01-01-20-42'. If not specified, nothing is added to the path.

**Format:** The records are written in a particular format. The format of the file must be any one of the following:
- avro
- csv
- delimited
- json
- parquet
- tsv

**Delimiter:** Use this when the format of the file is 'delimited'. 

**File System Properties:** Use this additional property with the InputFormat while reading the data. This is an advanced feature to specify any additional property that should be used with the sink. See [here](#file-system-properties) for details.

### File System Properties
This JSON string represents a map of properties that can be used for writing the data as required.

Sample use cases:

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
