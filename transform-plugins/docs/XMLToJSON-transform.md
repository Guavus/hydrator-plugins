# XML to JSON


Description
-----------
XML to JSON is a Guavus Enterprise Accelerator that accepts a field containing a properly-formatted XML string and converts it into a properly-formatted JSON string.

Use Case
--------
XML to JSON can be used with the Javascript transform for the parsing of complex XML documents into parts. Once the XML is a JSON string, you can convert it into a Javascript object using:

        var jsonObj = JSON.parse(input.jsonevent);


Configuration
-------------
**inputField:** Specify the input field containing an XML string to be converted to a JSON string.

**outputField:** Specify the output field where the JSON string will be stored. If it is not present in the output schema, it will be
added. (Macro-enabled)

## Sample Pipeline

```
    {
       "name": "XML to Json String",
       "plugin": {
       "name": "XMLToJSON",
       "type": "transform",
       "label": "XML to Json String",
       "artifact": {
          "name": "transform-plugins",
          "version": "2.1.1-SNAPSHOT_5.1.2047",
          "scope": "SYSTEM"
       },
       "properties": {
       "inputField": "record",
       "outputField": "data",
       "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"data\",\"type\":\"string\"}]}"
       }
    }

```

## Sample Input

|record |
|-------|
|`<employee id="1"> <name>John</name> <age>10</age> </employee>`|
|`<employee id="2"> <name>Daniel</name> <age>20</age> </employee>`|
|`<employee id="4"> <name>Charles</name> <age>19</age> </employee>`|
|`<employee id="5"> <name>Albert</name> <age>9</age> </employee>`|
|`<employee id="3"> <name>Sam</name> <age>14</age> </employee>`|


## Sample Output

|data   |
|-------|
|{"employee":{"name":"John","id":1,"age":10}}|
|{"employee":{"name":"Daniel","id":2,"age":20}}|
|{"employee":{"name":"Charles","id":4,"age":19}}|
|{"employee":{"name":"Albert","id":5,"age":9}}|
|{"employee":{"name":"Sam","id":3,"age":14}}|
