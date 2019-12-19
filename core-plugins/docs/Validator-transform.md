# Validator Transform

Description
-----------

Validator Transform is used for validating records in a dataset based on some configured rules.

The accelerator takes a dataset and a set of rules as inputs. These rules evaluate the input dataset using some predefined functions. If the specified rules are met,  then the accelerator passes the record to the next stage, else it removes the record from the output dataset. The output dataset contains all the input records for which the specified rules were evaluated to true.

**Output schema**: InputSchema

This table lists the methods available in CoreValidator that can be called from the Validator Transform:

| function                                        | description                                                    |
| ----------------------------------------------- | -------------------------------------------------------------- |
| maxLength(String input, int maxLength)          | Checks if the value length is less than or equal to the max    |
| maxValue(double val, double maxVal)             | Checks if the value is less than or equal to the max           |
| maxValue(long val, long maxVal)                 | Checks if the value is less than or equal to the max           |
| maxValue(int val, int maxVal)                   | Checks if the value is less than or equal to the max           |
| maxValue(float val, float maxVal)               | Checks if the value is less than or equal to the max           |
| minValue(double val, double minVal)             | Checks if the value is greater than or equal to the min        |
| minValue(long val, long minVal)                 | Checks if the value is greater than or equal to the min        |
| minValue(int val, int minVal)                   | Checks if the value is greater than or equal to the min        |
| minValue(float val, float minVal)               | Checks if the value is greater than or equal to the min        |
| minLength(String input, int length)             | Checks if the value length is greater than or equal to the min |


Function Descriptions
---------------------

maxLength
---------

Checks if the length of the value in the input column is less than or equal to the specified maximum length. The function takes a column name and the allowed maximum length as inputs.

Returns `True` if the column value's length is less than or equal to the specified maximum length.

maxValue
--------

Checks if the value in the input column is less than or equal to the specified maximum value. The function takes a column name and the allowed maximum value as inputs.

Returns `True` if the column's value is less than or equal to the specified maximum value.

minValue
--------

Checks if the value in the input column is greater than or equal to the specified minimum value. The function takes a column name and the allowed minimum value as inputs.

Returns `True` if the column's value is more than or equal to the specified minimum value.

minLength
---------

Checks if the length of the value in the input column is more than or equal to the specified minimum length. The function takes a column name and the allowed minimum length as inputs.

Returns `True` if the column value's length is more than or equal to the specified minimum length. 

Use Case
--------

The transform is used when you need to validate records. For example, you may want to validate records as being valid maxLength, maxValue, minLength and minValue.

For example, you may want to validate records in your dataset based on a rule that the `fname` column in the dataset must contain strings of length less than or equal to 3. For such case, you can use Validator and configure this accelerator with a rule that evaluates the `fname` column using the maxLength function. If the `fname` column contains a string of size greater than 3 for a data record, the specific record is dropped from the output dataset. The following sample pipeline configuration illustrates this example.

## Sample Pipeline

```
{
                "name": "Validator",
                "plugin": {
                    "name": "Validator",
                    "type": "transform",
                    "label": "Validator",
                    "artifact": {
                        "name": "core-plugins",
                        "version": "2.1.1-SNAPSHOT",
                        "scope": "SYSTEM"
                    },
                    "properties": {
                        "validators": "core",
                        "validationScript": "function isValid(input, context) {\n  var isValid = true;\n  var errMsg = \"\";\n  var errCode = 0;\n  var coreValidator = context.getValidator(\"coreValidator\");\n  var logger = context.getLogger();\n\n  if (coreValidator.maxLength(input.fname, 3)) {} else {\n    isValid = false;\n    errMsg = \"\" + input.fname + \" length is greater than the maximum\";\n    errCode = 11;\n  }\n\n  if (!isValid) {\n    var message = \"(\" + errCode + \") \" + errMsg;\n    logger.warn(\"Validation failed with error {}\", message);\n  }\n\n  return {\n    \"isValid\": isValid,\n    \"errorCode\": errCode,\n    \"errorMsg\": errMsg\n  };\n}"
                    }
                }
```

## Sample Input

| fname | lname | cost | zip code |
| ----- | ----- | ---- | -------- |
| bob   | smith | 50   | 12345    |
| bob   | jones | 30   | 23456    |
| alice | smith | 1.5  | 34567    |
| bob   | smith | 0.5  | 45678    |
| alice | smith | 30   | 56789    |

## Sample Output

| fname | lname | cost | zip code |
| ----- | ----- | ---- | -------- |
| bob   | smith | 50   | 12345    |
| bob   | jones | 30   | 23456    |
| bob   | smith | 0.5  | 45678    |
