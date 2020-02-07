# File Batch Source

## Description

File Batch Source is a plugin, which is used to read a distributed file system.

## Use Case

A scenario where you want to fetch the log files from HDFS every hour and then store the logs in a TimePartitionedFileSet. This can be achieved by making configurational changes as described in the following sections:


## Properties

**Reference Name:** This is a unique name used to identify this source for lineage, annotating metadata, so on.

**Path:** The path to read from. For example, hdfs:///tmp/sample.txt

**Format:** The format of the file must be any one of the following:
- avro
- blob: every input file is read in a separate record. This requires a schema that contains a field named 'body' of type 'bytes'.
- csv
- delimited
- json
- parquet
- text: The schema must contain a field named 'body' of type 'string'.

**Delimiter:** Use this when the format of the file is 'delimited'. Note: This value is not valid for any other format.

**Maximum Split Size:** The size is in bytes for each input partition.
The smaller partitions will increase the level of parallelism, but will require more resources and overhead.
The default value is 128MB.

**Regex Path Filter:** This file must match with the files available at the mentioned path to include it in the input. The complete 
file path is compared to the regular expression to filter file paths.

**Path Field:** Output column is added to place the path of the file that the record was read from.
If not specified, the file path will not be included in output records.
If specified, the field must exist in the output schema as a string.

**Path Filename Only:** Use the filename instead of the URI of the file path when a path field is given.
The default value is false.

**Read Files Recursively:** Read the files recursively from the path. The default value is false.

**Allow Empty Input:** Allow an input path that contains no data. When this property is set to false, the plugin
will show an error if there is no data to read. When this property is set to true, no error will appear and zero records will be read.

**File System Properties:** Use this additional property with the InputFormat while reading the data.

## Note

- It is mandatory to provide an output schema when using a format other than text. The default schema used in this plugin is for text format where the body represents line read from the file and offset represents offset of line in the file. 

- By default, this plugin support gzip(.gz) and bzip2(.bz2) compression formats. The user does not need to provide any additional configuration for compression, it automatically uncompresses the data based on the file extension.


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
            "delimiter": ","
          }
        }
      }
