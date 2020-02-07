# File Batch Source


Description
-----------
This source is used whenever you need to read from a distributed file system.
For example, you may want to read in log files from S3 every hour and then store
the logs in a TimePartitionedFileSet.


Properties
----------
**Reference Name:** Name used to uniquely identify this source for lineage, annotating metadata, etc.

**Path:** Path to read from. For example, s3a://<bucket>/path/to/input

**Format:** Format of the data to read.
The format must be one of 'avro', 'blob', 'csv', 'delimited', 'json', 'parquet', 'text', or 'tsv'.
If the format is 'blob', every input file will be read into a separate record.
The 'blob' format also requires a schema that contains a field named 'body' of type 'bytes'.
If the format is 'text', the schema must contain a field named 'body' of type 'string'.

**Delimiter:** Delimiter to use when the format is 'delimited'. This will be ignored for other formats.

**Maximum Split Size:** Maximum size in bytes for each input partition.
Smaller partitions will increase the level of parallelism, but will require more resources and overhead.
The default value is 128MB.

**Regex Path Filter:** The Regular Expressions that file paths must match in order to be included in the input. The full
file path is compared to the regular expression to filter file paths.

**Path Field:** Output field to place the path of the file that the record was read from.
If not specified, the file path will not be included in output records.
If specified, the field must exist in the output schema as a string.

**Path Filename Only:** Whether to only use the filename instead of the URI of the file path when a path field is given.
The default value is false.

**Read Files Recursively:** Whether files are to be read recursively from the path. The default value is false.

**Allow Empty Input:** Whether to allow an input path that contains no data. When set to false, the plugin
will error when there is no data to read. When set to true, no error will be thrown and zero records will be read.

**File System Properties:** Additional properties to use with the InputFormat when reading the data. See [here](#file-system-properties) for details.

## Sample Pipeline

    {
        "name": "File",
        "plugin": {
          "name": "File",
          "type": "batchsource",
          "label": "File",
          "artifact": {
            "name": "core-plugins",
            "version": "2.1.1-SNAPSHOT_5.1.216",
            "scope": "SYSTEM"
          },
          "properties": {
            "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"offset\",\"type\":\"long\"},{\"name\":\"body\",\"type\":\"string\"}]}",
            "referenceName": "ref_hdfs_src",
            "format": "text",
            "filenameOnly": "false",
            "recursive": "false",
            "ignoreNonExistingFolders": "false",
            "path": "/cdap/file_input",
            "delimiter": ",",
            "fileSystemProperties": "{\"mapreduce.output.fileoutputformat.compress\":\"true\",\"mapreduce.output.fileoutputformat.compress.codec\":\"org.apache.hadoop.io.compress.GzipCodec\"}"
          }
        }
      }
