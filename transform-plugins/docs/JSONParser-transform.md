# JSON Parser Transform

## Description

JSON Parser parses an input JSON event into a record. The input JSON event can either map string fields to values or it can be a complex nested JSON structure. The accelerator allows you to mention JSON paths for extracting fields from complex nested input JSON.

#### Configuration

    +=================================================================================================================================================+ 
    | Config  | Description                                                                                                                           |
    |---------|---------------------------------------------------------------------------------------------------------------------------------------|
    | field   | Specify the input field that should be parsed as a JSON record.                                                                     |
    | mapping | JSON Path Mapping specifying the output field name to input JSON path for extracting the field. Needed only when parsing the nested JSON. |
    | schema  | Specifies the output schema for the JSON Record                                                                                       |
    +=================================================================================================================================================+


### Use Case
JSON Parser reads the values of input column record by record, parses it as json, and then extracts the values from json as per the output schema.

**Note**: Values of input column should be a JSON String.

JSON Parser can be used to handle the following two cases:

##### 1. Parsing Simple JSON

Simple JSON (which is defined as a mapping from a key to value) parsing is achieved by specifying just the output schema fields. The field name in the output schema should be the same as the key in the input JSON. The type of the output field should also be the same as the input value type. No implicit conversions are performed on the JSON values.

When parsing a simple JSON, you don't need to specify json path mapping.

Here is an example of an event that is to be mapped to the output schema:

    {
       "id" : 1000,
       "first" : "Joltie",
       "last" : "Neutrino",
       "email_id" : "joltie.neutrino@gmail.com",
       "address" : "666 Mars Street",
       "city" : "Marshfield",
       "state" : "MR",
       "country" : "Marshyland",
       "zip" : 34553423,
       "planet" : "Earth"
    }


The output schema should be specified as:

    +==========================+
    | Field    | Type   | Null |
    |----------|--------|------|
    | id       | int    |      |
    | first    | string |      |
    | last     | string |      |
    | email_id | string |      |
    | address  | string |      |
    | city     | string |      |
    | state    | string |      |
    | country  | string |      |
    | zip      | long   |      |
    +==========================+

**Note:** The field "planet" has not been included in the output schema, which means that the field will be ignored and not processed when the JSON event is mapped. 

##### 2. Parsing Nested JSON

Parser also allows the extracting of fields from a complex nested JSON. In order to extract fields, it uses the JSON path mapping similar to the XPath expressions for XML. To extract fields using an expression in JSON, this accelerator uses the **JsonPath** library. The accelerator allows you to define the mapping from the output fieldname to the JSON path expression that is to be applied on the input to extract the value from the JSON event.

Consider that you have the following nested JSON:

    {
      "employee" : {
        "name" : {
          "first" : "Joltie",
          "last" : "Neutrino"
        },
        "email" : "joltie.neutrino@gmail.com",
        "address" :  {
          "street1" : "666, Mars Street",
          "street2" : "",
          "apt" : "",
          "city" : "Marshfield",
          "state" : "MR",
          "zip" : 34553423,
          "country" : "Marshyland"
        }
      }
    }


You could specify the mapping for extracting these fields from the input JSON event as:
 
  1. first
  2. last
  3. email
  4. street1
  5. city
  6. state
  7. zip
  8. country

The mappings in the accelerator will be:

    +================================================+
    | Output Field Name | Input JSON Path Expression |
    |-------------------|----------------------------|
    | first             | $.employee.name.first      |
    | last              | $.employee.name.last       |
    | email             | $.employee.email           |
    | street            | $.employee.address.street1 |
    | city              | $.employee.address.city    |
    | state             | $.employee.address.state   |
    | zip               | $.employee.address.zip     |
    | country           | $.employee.address.country |
    +================================================+
    
## JSONPath
As stated earlier, JSONPath is the query language for JSON. The following section explains how to use it to write the path mapping.

### Expression
A JSONPath expression specifies a path to an element (or a set of elements) in a JSON structure.

The "root member object" for parsing any JSON is referred to as ```$```, regardless of whether it's an array or an object.

JsonPath expressions can use the dot–notation ```$.employee.name``` or the bracket notation ```$['employee']['name']``` to access the nested JSON values.

JSONPath expressions supports different types of operators and functions that can help in parsing the complex nested JSON.

#### Supported Operators

These operators are supported:

    +========================================================================+
    | Operator          | Description                                        |
    |-------------------|----------------------------------------------------|
    | $                 | The root element of the query                      |
    | *                 | Wildcard                                           |
    | ..                | Deep scan                                          |
    | .<name>           | Dot notation representing child                    |
    | [?(<expression>)] | Filter expression, should be boolean result always |
    +========================================================================+

#### Supported Functions

The functions perform aggregations at the tail end of the path. The functions take the output of the expression as input for performing aggregations. The aggregation function can be only be applied where the expression results in an array. 

    +==================================================================+ 
    | Function | Type   | Description                                  |
    |----------|--------|----------------------------------------------|
    | min      | double | Minimum value of array of numbers            |
    | max      | double | Maximum value of array of numbers            |
    | avg      | double | Average value of array of numbers            |
    | stddev   | double | Standard deviation value of array of numbers |
    | length   | int    | Length of the array                          |
    +==================================================================+

#### Samples example for writing the JSONPath expression
See [https://github.com/json-path/JsonPath#path-examples](https://github.com/json-path/JsonPath#path-examples) for sample expression.

You can also do a hands on [http://jsonpath.herokuapp.com/](http://jsonpath.herokuapp.com/).


## Example

**Input Data**

```
+=====================================================================================+
| id | team  |                       employee_details                                 |
+=====================================================================================+
| 1  | HR    | {"employee":{"name":{"first":"Joltie","last":"Neutrino"},              |
|    |       | "email":"joltie.neutrino@gmail.com",                                   |
|    |       | "address":{"street1":"666,MarsStreet",                                 |
|    |       | "street2":"","apt":"","city":"Marshfield","state":"MR","zip":34553423, |
|    |       | "country":"Marshyland"}}}                                              |
| 2  | Admin | {"employee":{"name":{"first":"John","last":"Cena"},                    |
|    |       | "email":"jcena.wwe@gmail.com","address":{"street1":"5F,138","street2": |
|    |       | "AOC","apt":"apt12","city":"dummy_city 1","state":"NY",                |
|    |       | "zip":34511423,"country":"Marshyland"}}}                               |
+=====================================================================================+
```

**Plugin Configurations**

`To extract 'first', 'state' and 'email' fields from employee_details json`

`Json Path Mappings`

```
+=========================================+
| first       | $.employee.name.first     |
|-----------------------------------------|
| state       | $.employee.address.state  |
|-----------------------------------------|
| email       | $.employee.email          |
+=========================================+
```

```
{
    "name": "JSONParser",
    "plugin": {
        "name": "JSONParser",
        "type": "transform",
        "label": "JSONParser",
        "artifact": {
            "name": "transform-plugins",
            "version": "2.1.1-SNAPSHOT",
            "scope": "SYSTEM"
        },
        "properties": {
            "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"first\",\"type\":[\"string\",\"null\"]},{\"name\":\"state\",\"type\":[\"string\",\"null\"]},{\"name\":\"email\",\"type\":[\"string\",\"null\"]}]}",
            "mapping": "first:$.employee.name.first,state:$.employee.address.state,email:$.employee.email",
            "field": "employee_details"
        }
    }
}
```

**Output Data**

```
+=================================================+
|  first  |   state   |          email            |                                                      |
+=================================================+
| Joltie  |    MR     | joltie.neutrino@gmail.com |
| John    |    NY     | jcena.wwe@gmail.com       |
+=================================================+
```
