# Error Collector


Description
-----------
Error Collector is an open source accelerator that takes errors emitted from the previous stage of the pipeline, flattens them by adding a message, code, and stage to the record, and then outputs the result.

Use Case
--------
Consider a scenario wherein you want to use this accelerator to capture errors emitted from another stage and pass them along with all the error information flattened into the record. For example, you may want to connect a sink to this accelerator in order to store and later examine the error records. This can be achieved by making configurational changes as explained in the following section.

Properties
----------
**Error Message Column Name:** Specify the name of the error message field to be used in the output schema. By default, this value is 'errMsg'. If no value is specified, the error message will be dropped.

**Error Code Column Name:** Specify the name of the error code field to use in the output schema.
By default, this value is 'errCode'. If no value is specified, the error code will be dropped.

**stageField:** The name of the error stage field to use in the output schema.
By default, this value is 'errStage'. If no value is specified, the error stage will be dropped.


Example
-------
This example adds the error message, error code, and error stage as the 'errMsg', 'errCode', and 'errStage' fields.

    {
        "name": "ErrorCollector",
        "type": "errortransform",
        "properties": {
            "messageField": "errMsg",
            "codeField": "errCode",
            "stageField": "errStage"
        }
    }

For example, suppose the accelerator receives the following error record with error code 17, error message 'invalid', and error stage 'parser':

    +============================+
    | field name | type | value  |
    +============================+
    | A          | int  | 10     |
    | B          | int  | 20     |
    +============================+

It will add the error information to the record and produce the following output:

    +===============================+
    | field name | type   | value   |
    +===============================+
    | A          | int    | 10      |
    | B          | int    | 20      |
    | errMsg     | string | invalid |
    | errCode    | int    | 17      |
    | errStage   | string | parser  |
    +===============================+
