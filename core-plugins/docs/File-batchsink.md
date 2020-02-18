# File Sink


Description
-----------
Writes to a filesystem in various formats format.

For the

`csv`, `delimited`, and `tsv` formats: each record is written out as delimited text. Complex types like arrays, maps, and records will be converted to strings using their
                                       ``toString()`` Java method, so for practical use, fields should be limited to the
                                       string, long, int, double, float, and boolean types.

`avro` or `parquet` formats :  all types are supported.

`orc` format : string, long, int, double, float, boolean and array types are supported 

Properties
----------
**Reference Name:** Name used to uniquely identify this sink for lineage, annotating metadata, etc.

**Path:** Path to write to. For example, /path/to/output

**Path Suffix:** Time format for the output directory that will be appended to the path.
For example, the format 'yyyy-MM-dd-HH-mm' will result in a directory of the form '2015-01-01-20-42'.
If not specified, nothing will be appended to the path."

**Format:** Format to write the records in.
The format must be one of 'json', 'avro', 'parquet', 'csv', 'tsv', 'delimited' or 'orc'.

**Delimiter:** Delimiter to use if the format is 'delimited'.

**File System Properties:** Additional properties in json format to use with the OutputFormat when reading the data.
Advanced feature to specify any additional property that should be used with the sink.

## Configuration

| Configuration | Label | Required? | Default | Description |
| :------------ | :---- | :-------- | :------ | :---------- |
| `Reference Name` | label | Yes | File | Name used to uniquely identify this sink for lineage, annotating metadata, etc.|
| `Path` | Path | Yes | N/A | Path to write to. For example, /path/to/output. |
| `Path Suffix` | Path Suffix | Optional | yyyy-MM-dd-HH-mm | Time format for the output directory that will be appended to the path.For example, the format 'yyyy-MM-dd-HH-mm' will result in a directory of the form '2015-01-01-20-42'.If not specified, nothing will be appended to the path. |
| `Format` | Format | Yes | Json | Format to write the records in. The format must be one of 'json', 'avro', 'parquet', 'csv', 'tsv', 'delimited' or 'orc'.|
| `Delimiter` | Delimiter | Optional | N/A | Delimiter to use if the format is 'delimited'.|
| `File System Properties` | File System Properties | Optional | N/A | Additional properties in json format to use with the OutputFormat when reading the data.Advanced feature to specify any additional property that should be used with the sink.|

### Note
- For `date` as output data-type, this accelerator writes the corresponding int value (number of days since epoch) for non-binary file-formats (csv, json, tsv, delimited).
- For `time` and `timestamp` as output data-type, this accelerator writes long value for non-binary file-formats(csv, json, tsv, delimited). In case of `time` data-type, long value holds the number
of microseconds since midnight and for `timestamp` data-type, it holds the number of microseconds since UNIX epoch.

## Sample Input

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
              "delimiter": ","
            }
          }
    }

