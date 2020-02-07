# Row Denormalizer


Description
-----------
Row Denormalizer is an open source accelerator that converts raw data into denormalized data based on the key column in the input data. 
Using this accelerator, the user can specify the list of fields that must be used in the denormalized record, with an option to use an alias for the output field name. For example, 'ADDRESS' in input is mapped to 'addr' in the output schema. 

Use Case
--------
Consider a scenario wherein you have an input record that stores a variable set of custom attributes for an entity, and you want to denormalize it. Row Denormalizer takes the input record, denormalizes it on the basis of the key field, and then returns a denormalized table according to the output schema specified by you. The denormalized data is easier to query. The denormalization can be performed by making configurational changes in the accelerator as explained in the following section.

Properties
----------
The following pointers describe the fields as displayed in the accelerator properties dialog box.

**keyField:** Specify the name of the column in the input record which is to be used to group the raw data. For Example, id.

**nameField:** Specify the name of the column in the input record which contains the names of output schema columns. For example, if input records have columns 'id', 'attribute', 'value', and the 'attribute' column contains 'FirstName', 'LastName',
 'Address', the output record will have column names as 'FirstName', 'LastName', 'Address'.

**valueField:** Specify the name of the column in the input record which contains the values for output schema columns. For
example, if input records have columns 'id', 'attribute', 'value', and the 'value' column contains 'John', 'Wagh', 'NE Lakeside', the output record will have values for columns as 'FirstName', 'LastName', 'Address' as 'John', 'Wagh', 'NE Lakeside' respectively.

**outputFields:** Enter the list of the output fields to be included in the denormalized output.

**fieldAliases:** Enter the list of the output fields to be renamed. The key specifies the name of the required field, with its corresponding value representing the new name for that field.

**numPartitions:** Specify the number of partitions to use when grouping data. If not specified, the default value as per the execution framework is used

Conditions
----------
In case a field value is not present, it is considered as NULL.

Consider the following cases:

If keyfield('id') in the input record is NULL, then that particular record is filtered out.

If namefield('attribute') or valuefield('value') is not present for a particular keyfield('id') value, then the denormalized output value for that namefield is NULL.

If the user provides an output field which is not present in the input record, it is considered as NULL.

Example
-------
The accelerator takes input records that have column id, attribute, and value, then denormalizes it on the basis of id, and finally returns a denormalized table according to the output schema specified by the user.

    {
      "name": "RowDenormalizer",
      "type": "batchaggregator",
      "properties": {
         "outputFields": "Firstname,Lastname,Address",
         "fieldAliases": "Address:Office Address",
         "keyField": "id",
         "nameField": "attribute",
         "valueField": "value"
       }
    }

For example, suppose the aggregator receives the following input record:

    +======================================+
    | id        | attribute   | value      |
    +======================================+
    | joltie    | Firstname   | John       |
    | joltie    | Lastname    | Wagh       |
    | joltie    | Address     | NE Lakeside|
    +======================================+

The output records will contain all the output fields specified by the user, as shown below:

    +=========================================================+
    | id        | Firstname   | Lastname   |  Office Address  |
    +=========================================================+
    | joltie    | John        | Wagh       |  NE Lakeside     |
    +=========================================================+


Now, suppose the aggregator receives an input record with NULL values, as shown below:

    +======================================+
    | id        | attribute   | value      |
    +======================================+
    | joltie    | Firstname   | John       |
    | joltie    | Lastname    | Wagh       |
    | joltie    | Address     | NE Lakeside|
    | brett     | Firstname   | Brett      |
    |           | Lastname    | Lee        |
    | brett     | Address     | SE Lakeside|
    | bob       | Firstname   | Bob        |
    | bob       |             | Smith      |
    | bob       | Address     |            |
    +======================================+

The output records will contain all the output fields specified by user, as shown below:

    +=========================================================+
    | id        | Firstname   | Lastname   |  Office Address  |
    +=========================================================+
    | joltie    | John        | Wagh       |  NE Lakeside     |
    | brett     | Brett       |            |  SE Lakeside     |
    | bob       | Bob         |            |                  |
    +=========================================================+
