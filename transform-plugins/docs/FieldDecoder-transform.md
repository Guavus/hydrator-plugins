# Decoder Transform


Description
-----------
Decoder transform is used for decoding the fields of a dataset using a decoding method. You can specify multiple fields to be decoded using different decoding methods.
The available decoding methods are ``STRING_BASE64``, ``BASE64``, ``BASE32``, ``STRING_BASE32``, and ``HEX``.


Configuration
-------------
You need to specify the following configuration for the accelerator:

**decode:** Specifies the configuration for the fields to be decoded. In JSON configuration, 
this is specified as ``<field>:<decoder>[,<field>:<decoder>]*``.

**schema:** Specifies the output schema. The fields that are decoded will have the same field
name, but they will be of type ``BYTES`` or ``STRING``.

#### Note
- *Input fields that need to be decoded must be of type `String` or `Bytes` and non-nullable*


Use Case
--------
Consider a scenario wherein you want to decode some fields of your dataset which were previously encoded using an encoding scheme. For instance, you apply any source accelerator to take the input as rdd and apply the Decoder accelerator to decode the desired fields, then save the transformed output by applying any sink accelerator.


Sample
---------

### Input data

|name             |country             |subcountry        |geonameid|
|-----------------|--------------------|------------------|---------|
|les Escaldes     |IFXGI33SOJQQ====    |RXNjYWxkZXMtRW5nb3JkYW55|33303430303531|
|Andorra la Vella |IFXGI33SOJQQ====    |QW5kb3JyYSBsYSBWZWxsYQ==|33303431353633|
|Umm al Qaywayn   |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|VW1tIGFsIFFheXdheW4=|323930353934|
|Ras al-Khaimah   |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|UmHDisK8cyBhbCBLaGF5bWFo|323931303734|
|Khawr FakkÄ n    |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QXNoIFNow4QgcmlxYWg=|323931363936|
|Dubai            |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|RHViYWk=          |323932323233|
|Dibba Al-Fujairah|KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QWwgRnVqYXlyYWg=  |323932323331|
|Dibba Al-Hisn    |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QWwgRnVqYXlyYWg=  |323932323339|
|Sharjah          |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QXNoIFNow4QgcmlxYWg=|323932363732|
|Ar Ruways        |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QWJ1IERoYWJp      |323932363838|
|Al Fujayrah      |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QWwgRnVqYXlyYWg=  |323932383738|
|Al Ain           |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QWJ1IERoYWJp      |323932393133|
|Ajman            |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QWptYW4=          |323932393332|
|Adh Dhayd        |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QXNoIFNow4QgcmlxYWg=|323932393533|
|Abu Dhabi        |KVXGS5DFMQQEC4TBMIQEK3LJOJQXIZLT|QWJ1IERoYWJp      |323932393638|

Using the below decoding schemes:
- **Country**    -    `Base32` decoding.
- **subcountry** -    `Base64` decoding.
- **geonameid**  -    `Hex` decoding.

### Pipeline
```json
{
        "name": "Field Decoder",
        "plugin": {
          "name": "Decoder",
          "type": "transform",
          "label": "Field Decoder",
          "artifact": {
            "name": "transform-plugins",
            "version": "2.1.1-SNAPSHOT",
            "scope": "USER"
          },
          "properties": {
            "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"country\",\"type\":\"string\"},{\"name\":\"subcountry\",\"type\":\"string\"},{\"name\":\"geonameid\",\"type\":\"string\"}]}",
            "decode": "country:BASE32,subcountry:BASE64,geonameid:HEX"
          }
        }
}
```



### Decoded Output data
|name             |country             |subcountry        |geonameid|
|-----------------|--------------------|------------------|---------|
|les Escaldes     |Andorra             |Escaldes-Engordany|3040051  |
|Andorra la Vella |Andorra             |Andorra la Vella  |3041563  |
|Umm al Qaywayn   |United Arab Emirates|Umm al Qaywayn    |290594   |
|Ras al-Khaimah   |United Arab Emirates|RaÊ¼s al Khaymah  |291074   |
|Khawr FakkÄ n    |United Arab Emirates|Ash ShÄ riqah     |291696   |
|Dubai            |United Arab Emirates|Dubai             |292223   |
|Dibba Al-Fujairah|United Arab Emirates|Al Fujayrah       |292231   |
|Dibba Al-Hisn    |United Arab Emirates|Al Fujayrah       |292239   |
|Sharjah          |United Arab Emirates|Ash ShÄ riqah     |292672   |
|Ar Ruways        |United Arab Emirates|Abu Dhabi         |292688   |
|Al Fujayrah      |United Arab Emirates|Al Fujayrah       |292878   |
|Al Ain           |United Arab Emirates|Abu Dhabi         |292913   |
|Ajman            |United Arab Emirates|Ajman             |292932   |
|Adh Dhayd        |United Arab Emirates|Ash ShÄ riqah     |292953   |
|Abu Dhabi        |United Arab Emirates|Abu Dhabi         |292968   |
