# File (Source)


## Description
File is a Guavus Enterprise Accelerator that is used when you want to read from a distributed file system.

## Use Case
Consider a scenario wherein you want to fetch log files from HDFS every hour and then store the logs in a TimePartitionedFileSet. It can be achieved my making configurational changes as described in the sections below.

## Configuration
The following pointers describe the fields as displayed in the accelerator properties dialog box.

**Reference Name:** The name used to uniquely identify this source for lineage, annotating metadata, etc.

**Path:** The path to read from. For example, hdfs:///tmp/sample.txt

**Format:** The format of the data to be read.
The format must be one of 'avro', 'blob', 'csv', 'delimited', 'json', 'parquet', 'text', 'tsv' or 'orc'.
If the format is 'blob', every input file will be read into a separate record.
The 'blob' format also requires a schema that contains a field named 'body' of type 'bytes'.
If the format is 'text', the schema must contain a field named 'body' of type 'string'.

**Delimiter:** The delimiter to use when the format is 'delimited'. This will be ignored for other formats.

**Maximum Split Size:** The maximum size in bytes for each input partition.
Smaller partitions will increase the level of parallelism, but will require more resources and overhead.
The default value is 128MB.

**Regex Path Filter:** The Regular Expressions that file paths must match in order to be included in the input. The full
file path is compared to the regular expression to filter file paths.

**Path Field:** The output field in which you should place the path of the file that the record was read from.
If not specified, the file path will not be included in output records.
If specified, the field must exist in the output schema as a string.

**Path Filename Only:** Choose True or False based on whether you only want to use the filename instead of the URI of the file path when a path field is given. The default value is False.

**Read Files Recursively:** Choose True or False based on whether you want files to be read recursively from the path or not. The default value is False.

**Allow Empty Input:** Choose True or False based on whether you want to allow an input path that contains no data. When set to false, the plugin will throw an error if there is no data to read. When set to true, no error will be thrown and zero records will be read.

**File System Properties:** The additional properties in json format to use with the InputFormat when reading the data.


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

## How to get Output Schema?

The formats supported in the File accelerator can be categorised into: `hadoop` formats and `non-hadoop` formats.

1. For `hadoop` file formats - `orc`, `parquet`:

   `GetSchema` button is provided in the accelerator's configuration UI to help the user fetch the schema.
   If the value provided in `Path` config is a directory, then the schema would be fetched from any random file picked from the specified path matching extension `.orc` or `.parquet` (depending on selected `Format`).


2. For `non-hadoop` file formats - `csv`, `delimited`, `tsv`, `json`, `avro`:

   Pls use DataPrep to identify the schema of the file by applying `parse-as-<format>` directive or `Parse-><format>` on the `body` column.
   You can click on create pipeline, select batch, and then open wrangler stage and export the schema.
   Once exported, go back to file accelerator, select `Format` as the case is and import this schema file.



## Note

- It is mandatory to provide an output schema when using a format other than text. The default schema used in this plugin is for text format where the body represents line read from the file and offset represents offset of line in the file. 
- If the format is orc, then only string, long, int, double, float, boolean and array types are supported in output schema.
- By default, this plugin support gzip(.gz) and bzip2(.bz2) compression formats. The user does not need to provide any additional configuration for compression, it automatically un-compresses the data based on the file extension. 
