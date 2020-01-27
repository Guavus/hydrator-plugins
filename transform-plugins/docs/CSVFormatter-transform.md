# CSV Formatter Transform


Description
-----------
CSV Formatter is used to format a Structured Record as a CSV Record. 

The supported CSV Record formats are ``DELIMITED``, ``EXCEL``, ``MYSQL``, ``RFC4180``, and ``TDF``. When the format is ``DELIMITED``, one can specify different delimiters that a CSV Record should use for separating fields.

Use Case
--------
Consider a scenario wherein you want to convert each row of input data to a CSV record. It can be achieved by making confugurational changes as described in the following sections.


Configuration
-------------
**format:** Specifies the format of the CSV Record to be generated.

**delimiter:** Specifies the delimiter to be used to generate a CSV Record; this option is available when the format is specified as ``DELIMITED``.

**schema:** Specifies the output schema. Output schema should only have fields of the type String.

## Sample Pipeline

`Below is the Sample Pipeline for converting Structured Record to CSV using format DELIMITED and delimiter COMMA.`


```
    {
        "name": "CSVFormatter",
        "plugin": {
          "name": "CSVFormatter",
          "type": "transform",
          "label": "CSVFormatter",
          "artifact": {
            "name": "transform-plugins",
            "version": "2.1.1-SNAPSHOT_5.1.2047",
            "scope": "SYSTEM"
        },
          "properties": {
            "format": "DELIMITED",
            "delimiter": "COMMA",
            "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"body\",\"type\":\"string\"}]}"
        }
    }

```

## Sample Input

|name   |id |age|
|-------|---|---|
|john   |1  |10 |
|daniel |2  |20 |
|sam    |3  |50 |
|albert |5  |10 |
|charles|1  |10 |


## Sample Output

|body   |
|-------|
|john,1,10|
|daniel,2,20|
|sam,3,50|
|albert,5,10|
|charles,1,10|
