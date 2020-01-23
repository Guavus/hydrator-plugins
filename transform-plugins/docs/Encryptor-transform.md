# Field Encryptor


## Description
Field Encryptor is used to encrypt the fields present in the input records using a java keystore, which must be present on all nodes of the cluster.

## Use case
Consider a scenario wherein some of the input fields contain sensitive information and you want to encrypt those fields to hide them from other users.

## Configuration
**Fields to Encrypt:** Specify the fields to be encrypted; different fields should be separated by commas.

Note: 
- Only `int`, `long`, `float`, `double`, `string`, `boolean` and `bytes` formats are supported.

**transformation** The transformation algorithm in the format "algorithm/mode/padding" where mode and padding is optional. For example, `AES`, `RSA`, `AES/ECB/PKCS5Padding` `AES/CBC/PKCS5Padding`.

**ivHex:** Hex value of initialization vector if using the block cipher mode of operation.

**keystorePath:** Absolute path of the keystore file.
If the keystore path is configured in the property `program.container.dist.jars` of `cdap-site.xml`,
then the keystore file must be present on both the CDAP master nodes.
Else, the keystore file must be present on every slave node of the cluster.

**keystorePassword:** The password for the keystore.

**keystoreType:** The type of keystore. For example, JKS or JCEKS.

**keyAlias:** The alias of the key to be used in the keystore.

**keyPassword:** The password for the key to be used in the keystore.

Note: Do not use sink accelerators that store data in the textual format because Field Encryptor converts the field values to `bytes`, and the text-based sink accelerators will convert bytes to string at the time of writing the data.
Use any columnar format like ORC, Parquet etc.

### GetSchema
There is `GetSchema` button provided in accelerator's UI configuration. Use this button to auto-generate output schema.
The output schema is generated based on the input schema provided and the configuration specified.

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
      "transformation": "AES/CBC/PKCS5Padding",
      "keystorePath": "/tmp/aes-keystore.jck",
      "keystorePassword": "mystorepass",
      "keystoreType": "JCEKS",
      "keyAlias": "jceksaes",
      "keyPassword": "mykeypass",
      "ivHex": "813d92773b3d5067a3a31182d8a7d028"
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

#### Reference
This accelerator uses Java cryptography API internally for Encryption/Decryption. 
Refer to below articles for details:
- https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html
- https://www.veracode.com/blog/research/encryption-and-decryption-java-cryptography
- https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html
