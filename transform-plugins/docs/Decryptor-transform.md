# Decryptor Transform


## Description
Decrypts one or more fields in input records using a keystore 
that must be present on all nodes of the cluster.

**Note:**
- Only fields of type `bytes` are allowed for decryption.

## Configuration
**decryptFields** Specify the fields to decrypt, separated by commas

**schema** Schema to pull records from

**transformation** The transformation algorithm in the format "algorithm/mode/padding" where mode and padding is optional. For example, `AES`, `RSA`, `AES/ECB/PKCS5Padding` `AES/CBC/PKCS5Padding`.

**ivHex** Hex value of initialization vector if using the block cipher mode of operation.

**keystorePath** Absolute path of the keystore file.
If keystore path is configured in property `program.container.dist.jars` of `cdap-site.xml`
then keystore file must be present on both CDAP master nodes,
else keystore file must be present on every slave node of the cluster.

**keystorePassword** The password for the keystore

**keystoreType** The type of keystore; for example: JKS or JCEKS

**keyAlias** The alias of the key to use in the keystore

**keyPassword** The password for the key to use in the keystore

**schema** Specifies the output schema. Output columns are same as input columns except the types of decrypted fields.
User needs to manually set the type of decrypted fields.


## Example

**Input Data**

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

**Plugin Configuration**

`To decrypt 'name' and 'protocol' fields from input`
```
{
  "name": "Field Decrypter",
  "plugin": {
    "name": "Decryptor",
    "type": "transform",
    "label": "Field Decrypter",
    "artifact": {
      "name": "transform-plugins",
      "version": "2.1.1-SNAPSHOT",
      "scope": "SYSTEM"
    },
    "properties": {
      "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"name\",\"type\":[\"string\",\"null\"]},{\"name\":\"type\",\"type\":[\"string\",\"null\"]},{\"name\":\"destinationport\",\"type\":[\"string\",\"null\"]},{\"name\":\"protocol\",\"type\":[\"int\",\"null\"]}]}",
      "decryptFields": "name,protocol",
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
+==================================================+
|   name   |   type   | destinationport | protocol |
+==================================================+
|  C5089   | computer |   N46           |   6      |
|  C11573  | computer |   N10801        |   17     |
|  C5736   | computer |   111           |   17     |
|  C2270   | computer |   22            |   6      |
+==================================================+
```

#### Reference
This accelerator uses Java cryptography API internally for Encryption/Decryption. 
Refer to below articles for details:
- https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html
- https://www.veracode.com/blog/research/encryption-and-decryption-java-cryptography
- https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html