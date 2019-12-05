# Encoder Transform


Description
-----------
Encodes configured fields. Multiple fields can be specified to be encoded using different encoding methods.
Available encoding methods are ``BASE64``, ``BASE32``, and ``HEX``. `STRING_BASE32` and `STRING_BASE64` are also supported but they work similar to `BASE32` and `BASE64`, only difference
is that they first encode the values as string and then convert it to bytes instead of directly converting to encoded bytes.


Configuration
-------------
**encode:** Specifies the configuration for encode fields; in JSON configuration, 
this is specified as ``<field>:<encoder>[,<field>:<encoder>]*``.

**schema:** Specifies the output schema; the fields that are encoded will have the same field name 
but they will be of type ``BYTES`` or ``STRING``.

**Note**: *Input fields can not be nullable.*


Sample
---------
### Input data
|name             |country             |subcountry        |geonameid|
|-----------------|--------------------|------------------|---------|
|les Escaldes     |Andorra             |Escaldes-Engordany|3040051  |
|Andorra la Vella |Andorra             |Andorra la Vella  |3041563  |
|Umm al Qaywayn   |United Arab Emirates|Umm al Qaywayn    |290594   |
|Ras al-Khaimah   |United Arab Emirates|RaÊ¼s al Khaymah  |291074   |
|Khawr FakkÄn    |United Arab Emirates|Ash ShÄriqah     |291696   |
|Dubai            |United Arab Emirates|Dubai             |292223   |
|Dibba Al-Fujairah|United Arab Emirates|Al Fujayrah       |292231   |
|Dibba Al-Hisn    |United Arab Emirates|Al Fujayrah       |292239   |
|Sharjah          |United Arab Emirates|Ash ShÄriqah     |292672   |
|Ar Ruways        |United Arab Emirates|Abu Dhabi         |292688   |
|Al Fujayrah      |United Arab Emirates|Al Fujayrah       |292878   |
|Al Ain           |United Arab Emirates|Abu Dhabi         |292913   |
|Ajman            |United Arab Emirates|Ajman             |292932   |
|Adh Dhayd        |United Arab Emirates|Ash ShÄriqah     |292953   |
|Abu Dhabi        |United Arab Emirates|Abu Dhabi         |292968   |

For above data using below encoding schemes:

- **country**     -   `Base32` encoding.
- **subcountry**  -   `Base64` encoding.
- **geonameid**   -   `Hex` encoding.
 

### Sample Pipeline

```json
{
        "name": "Field Encoder",
        "plugin": {
          "name": "Encoder",
          "type": "transform",
          "label": "Field Encoder",
          "artifact": {
            "name": "transform-plugins",
            "version": "2.1.1-SNAPSHOT",
            "scope": "USER"
          },
          "properties": {
            "schema": "{\"type\":\"record\",\"name\":\"etlSchemaBody\",\"fields\":[{\"name\":\"name\",\"type\":[\"string\",\"null\"]},{\"name\":\"country\",\"type\":[\"string\",\"null\"]},{\"name\":\"subcountry\",\"type\":[\"string\",\"null\"]},{\"name\":\"geonameid\",\"type\":[\"string\",\"null\"]}]}",
            "encode": "name:NONE,country:BASE32,subcountry:BASE64,geonameid:HEX"
          }
        },
        ...
}

```

### Output data
|name             |country             |subcountry        |geonameid|
|-----------------|--------------------|------------------|---------|
|les Escaldes     |QW5kb3JyYQ==        |RXNjYWxkZXMtRW5nb3JkYW55|33303430303531|
|Andorra la Vella |QW5kb3JyYQ==        |QW5kb3JyYSBsYSBWZWxsYQ==|33303431353633|
|Umm al Qaywayn   |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|VW1tIGFsIFFheXdheW4=|323930353934|
|Ras al-Khaimah   |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|UmHDisK8cyBhbCBLaGF5bWFo|323931303734|
|Khawr FakkÄ n    |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QXNoIFNow4QgcmlxYWg=|323931363936|
|Dubai            |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|RHViYWk=          |323932323233|
|Dibba Al-Fujairah|VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QWwgRnVqYXlyYWg=  |323932323331|
|Dibba Al-Hisn    |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QWwgRnVqYXlyYWg=  |323932323339|
|Sharjah          |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QXNoIFNow4QgcmlxYWg=|323932363732|
|Ar Ruways        |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QWJ1IERoYWJp      |323932363838|
|Al Fujayrah      |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QWwgRnVqYXlyYWg=  |323932383738|
|Al Ain           |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QWJ1IERoYWJp      |323932393133|
|Ajman            |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QWptYW4=          |323932393332|
|Adh Dhayd        |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QXNoIFNow4QgcmlxYWg=|323932393533|
|Abu Dhabi        |VW5pdGVkIEFyYWIgRW1pcmF0ZXM=|QWJ1IERoYWJp      |323932393638|

