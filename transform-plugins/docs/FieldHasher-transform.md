# Field Hasher Transform


## Description
Field Hasher is an open source accelerator that hashes fields using a digest algorithm such as ``MD2``, ``MD5``, ``SHA1``, ``SHA256``, ``SHA384``, or ``SHA512``.


## Configuration

The following pointers describe the fields as displayed in the accelerator properties dialog box.


**Hasher:** Select a hashing algorithm.

**Fields:** Specify the fields to be hashed. Only `String` values are allowed. The hashed output will also be of the type String.

***Note:*** The output schema of this accelerator must be the same as the input schema.

## Example

**Input Data**

```
+==========================================+
| duration |  name  | connects | bytecount |
+==========================================+
|      9   | C5089  |   C24735 |   152     |
|     13   | C11573 |   C5736  |   272     |
|      7   | C20101 |   C5720  |   162     |
+==========================================+
```

**Configuration for Hashing**

To hash the 'name' and 'connects' fields from the input using MD5 algorithm, the configuration is as follows:
```
{
    "name": "Field Hasher",
    "plugin": {
        "name": "FieldHasher",
        "type": "transform",
        "label": "Field Hasher",
        "artifact": {
            "name": "transform-plugins",
            "version": "2.1.1-SNAPSHOT_5.1.2047",
            "scope": "SYSTEM"
        },
        "properties": {
            "hash": "MD5",
            "fields": "name,connects"
        }
    }
}
```

**The Output Data is as follows**

```
+============================================================================================+
| duration |                name              |             connects             | bytecount |
+============================================================================================+
|      9   | 9cfdb22658f71105e99a42f40b3dde45 | 4c9535e27a40026a6dae794c796af73b |   152     |
|     13   | 2d399c093016bd2fca48195705b482ec | 3dd975eb76b9e12a40cc8fc3f9b7105e |   272     |
|      7   | e3bcdc59184318064887fcb7e2d4320e | 3d6ab34a324aba9febe6c2c0bdaea207 |   162     |
+============================================================================================+
```
