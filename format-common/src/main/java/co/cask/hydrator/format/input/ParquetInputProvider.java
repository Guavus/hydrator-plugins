/*
 * Copyright © 2018 Cask Data, Inc.
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

package co.cask.hydrator.format.input;

import co.cask.cdap.api.data.schema.Schema;
import co.cask.hydrator.format.AvroSchemaConverter;
import com.google.common.base.Strings;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.schema.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import javax.annotation.Nullable;

/**
 * Provides Parquet formatters.
 */
public class ParquetInputProvider implements FileInputFormatterProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ParquetInputProvider.class);

    @Nullable
    @Override
    public Schema getSchema(@Nullable String pathField) {
        try {
            if (Strings.isNullOrEmpty(pathField)) {
                throw new IllegalArgumentException("Path Field should contain a valid path for fetching Schema");
            }
            Path path = new Path(pathField);
            Configuration conf = new Configuration();
            conf.setBoolean(AvroSchemaConverter.ADD_LIST_ELEMENT_RECORDS, false);
            FileSystem fs = FileSystem.get(conf);
            if(!fs.exists(path)) {
                throw new IllegalArgumentException("Path: " + pathField + " doesn't exist for fetching Schema");
            }
            String parquetFile = pathField;
            if(fs.isDirectory(path)){
                RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fs.listFiles(path, true);
                Map<String, Long> map = new java.util.HashMap<String, Long>();

                while (locatedFileStatusRemoteIterator.hasNext()){
                    LocatedFileStatus fileStatus = locatedFileStatusRemoteIterator.next();
                    if(fileStatus.isFile() && fileStatus.getPath().toString().endsWith(".parquet") && (fileStatus.getLen() > 0)){
                        map.put(fileStatus.getPath().toString(),fileStatus.getModificationTime());
                    }
                }

                if(!map.isEmpty()) {
                    LinkedHashMap<String, Long> reverseSortedMap = new LinkedHashMap<>();

                    map.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

                    if (reverseSortedMap.keySet().iterator().hasNext()) {
                        parquetFile = reverseSortedMap.keySet().iterator().next();
                        System.out.println("Using latest parquet file: " + parquetFile + " with modification time: " + reverseSortedMap.get(parquetFile) + " for getSchema");
                    }
                }

                if(parquetFile.equals(pathField)){
                    throw new RuntimeException("No valid parquet files present inside directory: " + pathField + " to fetch schema ");
                }
            }
            path = new Path(parquetFile);
            ParquetMetadata readFooter = ParquetFileReader.readFooter(conf, path, ParquetMetadataConverter.NO_FILTER);
            MessageType mt = readFooter.getFileMetaData().getSchema();
            org.apache.avro.Schema avroSchema = new AvroSchemaConverter(conf).convert(mt);
            return Schema.parseJson(avroSchema.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException("Error in reading parquet schema => " + e.getMessage(), e);
        }
    }


    @Override
    public FileInputFormatter create(Map<String, String> properties, @Nullable Schema schema) {
        return new ParquetInputFormatter(schema);
    }
}
