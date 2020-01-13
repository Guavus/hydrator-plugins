# CSV Parser Transform


Description
-----------
CSV Parser is a Guavus Enterprise Accelerator that parses an input field as a CSV Record into a Structured Record. 

This accelerator supports parsing of a multi-line CSV Record into multiple Structured Records. Different formats of CSV Record can be parsed using this CSV Parser. It supports these CSV Record types: ``DEFAULT``, ``Tab Delimited``, ``Pipe Delimited`` and ``Custom Delimeted``.

Use Case
---------
Consider a scenario where you want to parse each record as a csv based on a delimiter.

Configuration
-------------
**format:** Specify the format of the csv record that the input should be parsed as.

**delimiter:** Custom delimiter to be used for parsing the fields. The custom delimiter can only be specified by 
selecting the option 'Custom' from the format drop-down.

**field:** Specify the input field that should be parsed as a CSV Record. 
Input records with a null input field propagate all other fields and set fields that
would otherwise be parsed by the CSVParser to null.

**schema:** Specifies the output schema of the CSV Record.

## Sample Input

The input will be a csv file which will be parsed by the CSV Parser accelerator.

    id,test1,test2,servicetac,operstatus,itseverity
    0,testA,testB,0.0,Active,1
    1,testA,testB,1.0,Active,2
    2,testA,testB,2.0,Active,3
    3,testA,testB,3.0,Active,4
    4,testA,testB,4.0,Active,5
    5,testA,testB,5.0,Active,6
    
## PLugin Configuration Details
    {
        "name": "CSVParser",
        "plugin": {
          "name": "CSVParser",
          "type": "transform",
          "label": "CSVParser",
          "properties": {
            "format": "DEFAULT",
            "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"offset\",\"type\":\"long\"},{\"name\":\"body\",\"type\":\"string\"}]}",
            "field": "body"
          }
        }
    }
    
## Sample Input for Custom Delimeter

The input will be a csv file which will be parsed by the CSV Parser accelerator.

    id;test1;test2;servicetac;operstatus;itseverity
    0;testA;testB;0.0;Active;1
    1;testA;testB;1.0;Active;2
    2;testA;testB;2.0;Active;3
    3;testA;testB;3.0;Active;4
    4;testA;testB;4.0;Active;5
    5;testA;testB;5.0;Active;6
    
## Plugin Configuration Details for Custom Delimeter
    {
        "name": "CSVParser",
        "plugin": {
          "name": "CSVParser",
          "type": "transform",
          "label": "CSVParser",
          "properties": {
            "format": "Custom",
            "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"offset\",\"type\":\"long\"},{\"name\":\"body\",\"type\":\"string\"}]}",
            "delimiter": ";",
            "field": "body"
          }
        }
    }
    
## Sample Output
After parsing the sample input by CSV Parser, the csv will be parsed into a record.

    +==========================================================+
    | id | test1 | test2 | servicetac | operstatus | itseverity|
    |----|-------|-------|------------|------------|-----------|
    | 0  | testA |testB  |    0.0     |  Active    |     1     |
    | 1  | testA |testB  |    1.0     |  Active    |     2     |
    | 2  | testA |testB  |    2.0     |  Active    |     3     |
    | 3  | testA |testB  |    3.0     |  Active    |     4     |
    | 4  | testA |testB  |    4.0     |  Active    |     5     |
    | 5  | testA |testB  |    5.0     |  Active    |     6     |
    +===========================================================+
