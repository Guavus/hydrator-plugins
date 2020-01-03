# Field Encryptor


## Description
Field Encryptor is a Guavus enterprise Accelerator that is used to encrypt the fields present in the input records using a java keystore, which must be present on all nodes of the cluster.


## Configuration
**Fields to Encrypt** Specify the fields to be encrypted; different fields should be separated by commas.

***Note:*** Only `int`, `long`, `float`, `double`, `string` and `bytes` formats are supported.

**transformation** Transformation algorithm/mode/padding. For example, AES/CBC/PKCS5Padding.

**ivHex** The initialization vector if using CBC mode.

**keystorePath** Absolute path of the keystore file.
If keystore path is configured in property `program.container.dist.jars` of `cdap-site.xml`
then the keystore file must be present on both the CDAP master nodes,
else the keystore file must be present on every slave node of the cluster.

**keystorePassword** The password for the keystore.

**keystoreType** The type of keystore. For example, JKS or JCEKS.

**keyAlias** The alias of the key to be used in the keystore.

**keyPassword** The password for the key to be used in the keystore.


**Note**: Do not use the sink accelerators that store data in textual format because Field Encryptor converts the field values to `bytes` and the text-based sink accelerators will convert bytes to string at the time of writing the data.
Use any columnar format like ORC, Parquet etc. 


## Example

**Input Data**

```
+==================================================+
|   name   |   type   | destinationport | protocol |
+==================================================+
|  C5089   | computer |   N46           |   6      |
|  C11573  | computer |   N10801        |   17     |
|  C5736   | computer |   111           |   17     |
|  C2270   | computer |   22            |   6      |
+==================================================+
```

**Plugin Configuration**

`To encrypt 'name' and 'protocol' fields from input`
```
{
  "name": "Field Encryptor",
  "plugin": {
    "name": "Encryptor",
    "type": "transform",
    "label": "Field Encryptor",
    "artifact": {
      "name": "transform-plugins",
      "version": "2.1.1-SNAPSHOT",
      "scope": "SYSTEM"
    },
    "properties": {
      "encryptFields": "name,protocol",
      "transformation": "AES",
      "keystorePath": "/tmp/aes-keystore.jck",
      "keystorePassword": "mystorepass",
      "keystoreType": "JCEKS",
      "keyAlias": "jceksaes",
      "keyPassword": "mykeypass"
    }
  }
}
```

**Output Data**

```
+=============================================================================================================================================================+
|                      name                                      |   type   |  destinationport |                    protocol                                  |
+=============================================================================================================================================================+
| [-9,54,93,-123,-112,-61,23,30,-14,14,-39,122,108,-81,-122,-24] | computer |    N46           | [-81,56,-98,120,-26,-51,-75,-120,6,-13,-36,3,-62,62,-42,-24] |
| [-3,82,-72,-89,16,35,-84,-86,-94,-94,30,-83,-19,36,54,-23]     | computer |    N10801        | [-122,49,80,99,36,7,104,108,-46,48,-30,50,14,19,122,113]     |
| [83,-52,-46,83,-80,-87,-114,19,42,38,61,-120,-122,18,83,-18]   | computer |    111           | [-122,49,80,99,36,7,104,108,-46,48,-30,50,14,19,122,113]     |
| [58,-121,68,-21,91,52,57,-107,127,30,123,-103,89,-45,69,74]    | computer |    22            | [-81,56,-98,120,-26,-51,-75,-120,6,-13,-36,3,-62,62,-42,-24] |
+=============================================================================================================================================================+
```
