# Union Splitter


Description
-----------
Union Splitter is an open source accelerator that is used to split data by a union schema. The logic to be performed downstream will depend on the type of data.

Union splitter emits records to different ports depending on the schema of a particular field or of 
the entire record (ports refer to the different output streams; for example, one can be double, another can 
be int, and so on). If no field is specified, each record is emitted to a port named after the name of 
the record schema. If a field is specified, the schema for that field must be a union of supported schemas. 
All schemas except maps, arrays, unions and enums are supported. For each input record, the value of that 
field is examined and emitted to a port corresponding to its schema in the union.


For record schemas, the output port is the name of the record schema. For simple types, the output port is the schema type in lowercase ('null', 'bool', 'bytes', 'int', 'long', 'float', 'double', or 'string').


Properties
----------
**unionField:** The union field to split on. The schema for the field must be a union of supported schemas.
All schemas except maps, arrays, unions and enums are supported. Note that nulls are supported,
which means all nulls will get sent to the 'null' port.


The following pointers describe the fields as displayed in the accelerator properties dialog box.

**unionField:** Select the union field to perform the split on. The schema for the field must be a union of supported schemas.
All schemas except maps, arrays, unions, and enums are supported. Note that nulls are supported, which means all nulls will get sent to the 'null' port.

**modifySchema:** Select one of the options between 'True' and 'False' depending on whether you want to modify the output schema to remove the union. For example, suppose the field 'x' is a union of int and long. If modifySchema is true, the schema for field 'x' will just be an int for the 'int' port and long for the 'long' port. If modifySchema is false, the output schema for each port
is the same as the input schema. The default selection is 'true'.


Example
-------
Suppose the union splitter is configured to perform the split on the 'item' field:

    {
        "name": "UnionSplitter",
        "type": "splittertransform",
        "properties": {
            "field": "item",
            "modifySchema": "true"
        }
    }


Now, suppose the splitter receives records with the following schema:

    +=================================+
    | name  | type                    |
    +=================================+
    | id    | long                    |
    | user  | string                  |
    | item  | [ int, long, itemMeta ] |
    +=================================+

Along with the schema above, the 'item' field as a union of int, long and a record named 'itemMeta' is also received:

    +=================================+
    | name  | type                    |
    +=================================+
    | id    | long                    |
    | desc  | string                  |
    +=================================+

This means the union splitter will have three output ports, one for each schema in the union.

If a record contains an integer for the 'item' field, it is emitted to the 'int' port with the following output schema:

    +===============================+
    | name  | type                  |
    +===============================+
    | id    | long                  |
    | user  | string                |
    | item  | int                   |
    +===============================+

If a record contains a long for the 'item' field, it is emitted to the 'long' port with the following output schema:

    +===============================+
    | name  | type                  |
    +===============================+
    | id    | long                  |
    | user  | string                |
    | item  | long                  |
    +===============================+

If a record contains a StructuredRecord with the itemMeta schema for the 'item' field, it is emitted to the 'itemMeta' port with the following output schema:

    +===============================+
    | name  | type                  |
    +===============================+
    | id    | long                  |
    | user  | string                |
    | item  | itemMeta              |
    +===============================+
