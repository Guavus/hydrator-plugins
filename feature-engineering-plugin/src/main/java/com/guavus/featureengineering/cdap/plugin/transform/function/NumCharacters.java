/*
 * Copyright © 2016 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.guavus.featureengineering.cdap.plugin.transform.function;

import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.data.schema.Schema;

/**
 * Method to get day of week from timestamp.
 * 
 * @author bhupesh.goel
 *
 */
public class NumCharacters implements TransformFunction<Integer> {

    private final String fieldName;
    private final Schema outputSchema;

    /**
     * 
     */
    public NumCharacters(String fieldName, Schema fieldSchema) {
        this.fieldName = fieldName;
        this.outputSchema = Schema.of(Schema.Type.INT);
    }

    @Override
    public Integer applyFunction(StructuredRecord record) {
        Object val = record.get(fieldName);
        if (val == null) {
            return 0;
        }
        return (val.toString()).length();
    }

    @Override
    public Schema getOutputSchema() {
        return outputSchema;
    }

}