# Validator Transform


Description
-----------
Validates a record, writing to an error dataset if the record is invalid.
Otherwise it passes the record on to the next stage.

This table lists the methods available in CoreValidator that can be called from the ValidatorTransform:

    +==================================================================================================================+
    | function                                        | description                                                    |
    +==================================================================================================================+
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
    +==================================================================================================================+


Functions Description
---------------------

maxLength
---------

Checks if the value's length is less than or equal to the max.
It perform operation on the column we selected. 
Max value should be the maximum length with which validation performs on column value.
Returns True if the column value's length is less than the specified maximum value. 

maxValue
--------

Checks if the value is less than or equal to the max.
Max value should be the maximum value with which validation performs on column value.
Returns True if the column value's length is <= the specified maximum value.

minValue
--------

Checks if the value is greater than or equal to the min.
Min value should be the minimum value with which validation performs on column value.
Returns True if the column value's length is >= the specified minimum value.

minLength
---------

Checks if the value's length is greater than or equal to the min.
Min value should be the minimum length with which validation performs on column value.
Returns True if the column value's length is more than the specified minimum value. 

Use Case
--------
The transform is used when you need to validate records. For example, you may want to
validate records as being valid maxLength, maxValue, minLength and minValue if they are not
valid.
