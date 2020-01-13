# JSON Formatter Transform


Description
-----------
JSON Formatter is used to format a Structured Record as a JSON Object. This accelerator is used to convert the Structured Record to a JSON object and write to the output record. The output record schema is a single field,
either of the type ``STRING`` or the type ``BYTE``.


Use Case
--------
Consider a scenario wherein you want to convert each row of the input record to json data. This can be achieved by configuring the accelerator as described in the following sections.

Configuration
-------------
**schema:** Specifies the output schema, a single field of either the type ``STRING`` or the type ``BYTES``.


## Sample Pipeline

```
    {
       "name": "JSONFormatter",
       "plugin": {
       "name": "JSONFormatter",
       "type": "transform",
       "label": "JSONFormatter",
       "artifact": {
           "name": "transform-plugins",
           "version": "2.1.1-SNAPSHOT_5.1.2047",
           "scope": "SYSTEM"
       },
       "properties": {
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
|albert |5  |11 |
|charles|4  |10 |


## Sample Output

|body   |
|-------|
|{"name":"john","id":"1","age":"10"}|
|{"name":"daniel","id":"2","age":"20"}|
|{"name":"sam","id":"3","age":"50"}|
|{"name":"albert","id":"5","age":"11"}|
|{"name":"charles","id":"4","age":"10"}|
