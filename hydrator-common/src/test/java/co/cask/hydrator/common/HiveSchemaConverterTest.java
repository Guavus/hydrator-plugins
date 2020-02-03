package co.cask.hydrator.common;

import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.api.data.schema.UnsupportedTypeException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class HiveSchemaConverterTest {
    final Schema recordSchema = Schema.recordOf("record", Schema.Field.of("intField", Schema.of(Schema.Type.INT)));

    private Schema schema = Schema.recordOf(
            "record",
            Schema.Field.of("boolField", Schema.nullableOf(Schema.of(Schema.Type.BOOLEAN))),
            Schema.Field.of("intField", Schema.of(Schema.Type.INT)),
            Schema.Field.of("nullField", Schema.of(Schema.Type.NULL)),
            Schema.Field.of("longField", Schema.nullableOf(Schema.of(Schema.Type.LONG))),
            Schema.Field.of("floatField", Schema.nullableOf(Schema.of(Schema.Type.FLOAT))),
            Schema.Field.of("doubleField", Schema.nullableOf(Schema.of(Schema.Type.DOUBLE))),
            Schema.Field.of("bytesField", Schema.nullableOf(Schema.of(Schema.Type.BYTES))),
            Schema.Field.of("stringField", Schema.nullableOf(Schema.of(Schema.Type.STRING))),
            Schema.Field.of("mapField", Schema.mapOf(Schema.of(Schema.Type.INT), Schema.of(Schema.Type.STRING))),
            Schema.Field.of("arrayField", Schema.arrayOf(Schema.of(Schema.Type.INT))),
            Schema.Field.of("nestedField", Schema.mapOf(Schema.of(Schema.Type.INT), Schema.arrayOf(Schema.of(Schema.Type.STRING))))
    );


    // Orc schema string for above cdap schema.
    private String orcSchema = "struct<boolField:boolean,intField:int,longField:bigint,floatField:float,doubleField:double,bytesField:binary,stringField:string,mapField:map<int,string>,arrayField:array<int>,nestedField:map<int,array<string>>>";

    @Test
    public void testAppendType() {

        StringBuilder builder;
        try {
            // Test for a simple record
            builder = new StringBuilder();
            HiveSchemaConverter.appendType(builder, recordSchema);
            Assert.assertEquals("struct<intField:int>", builder.toString());

            // Test when first fields is null field.
            builder = new StringBuilder();
            HiveSchemaConverter.appendType(builder, Schema.recordOf("asdf", Schema.Field.of("nullField", Schema.of(Schema.Type.NULL)), Schema.Field.of("longField", Schema.nullableOf(Schema.of(Schema.Type.LONG)))));
            Assert.assertEquals("struct<longField:bigint>", builder.toString());

            // Test when last fields is null fields
            builder = new StringBuilder();
            HiveSchemaConverter.appendType(builder, Schema.recordOf("asdf", Schema.Field.of("intField", Schema.of(Schema.Type.INT)),  Schema.Field.of("nullField", Schema.of(Schema.Type.NULL))));
            Assert.assertEquals("struct<intField:int>", builder.toString());

            // Test for complex schema.
            builder = new StringBuilder();
            HiveSchemaConverter.appendType(builder, schema);
            Assert.assertEquals(orcSchema, builder.toString());

            // test for complex nested schema.
            builder = new StringBuilder();
            HiveSchemaConverter.appendType(builder, getNestedComplexSchema());
            String orcSchema = builder.toString();
            Assert.assertEquals(getNestedOrcComplexSchema(), orcSchema);

        } catch (UnsupportedTypeException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Creates a complex nested schema with below structure.
     * {
     *     schema(record type): {
     *          // other fields
     *         inner-schema(record type): {
     *             // other fields prefixed with "inner_"
     *             record-schema(record type): {
     *                 // Only one field of type `int`
     *             }
     *         }
     *     }
     * }
     * @return
     */
    private Schema getNestedComplexSchema() {
        List<Schema.Field> outerFields = new LinkedList<>();

        List<Schema.Field> innerFields = new LinkedList<>();
        // Copy all fields from schema and rename it.
        for(Schema.Field field: schema.getFields()) {
            innerFields.add(Schema.Field.of("inner_" + field.getName(), field.getSchema()));
        }

        // Add one more field of type record in inner schema.
        innerFields.add(Schema.Field.of("inner_recordField", recordSchema));

        Schema innerSchema = Schema.recordOf("recordField", innerFields);

        outerFields.addAll(schema.getFields());
        outerFields.add(Schema.Field.of("recordField", innerSchema));

        return Schema.recordOf("complexNestedSchema", outerFields);
    }

    /**
     * Returns orc schema representation of complex nested schema
     * @return
     */
    private String getNestedOrcComplexSchema() {
        StringBuilder builder = new StringBuilder();
        // output schema
        builder.append("struct<boolField:boolean,intField:int,longField:bigint,floatField:float,doubleField:double,bytesField:binary,stringField:string,mapField:map<int,string>,arrayField:array<int>,nestedField:map<int,array<string>>,");
        // inner schema
        builder.append("recordField:struct<inner_boolField:boolean,inner_intField:int,inner_longField:bigint,inner_floatField:float,inner_doubleField:double,inner_bytesField:binary,inner_stringField:string,inner_mapField:map<int,string>,inner_arrayField:array<int>,inner_nestedField:map<int,array<string>>,");
        // inner schema with in inner schema
        builder.append("inner_recordField:struct<intField:int>>");
        // close final parenthesise
        builder.append(">");
        return builder.toString();
    }
}
