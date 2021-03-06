/*
 * Copyright © 2015-2019 Cask Data, Inc.
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

package io.cdap.plugin.batch.sink;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.cdap.api.annotation.Requirements;
import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.dataset.lib.KeyValue;
import io.cdap.cdap.api.dataset.lib.cube.Cube;
import io.cdap.cdap.api.dataset.lib.cube.CubeFact;
import io.cdap.cdap.etl.api.Emitter;
import io.cdap.cdap.etl.api.batch.BatchRuntimeContext;
import io.cdap.cdap.etl.api.batch.BatchSink;
import io.cdap.plugin.common.CubeSinkConfig;
import io.cdap.plugin.common.CubeUtils;
import io.cdap.plugin.common.Properties;
import io.cdap.plugin.common.StructuredRecordToCubeFact;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link BatchSink} that writes data to a {@link Cube} dataset.
 * <p/>
 * This {@link BatchCubeSink} takes a {@link StructuredRecord} in, maps it to a {@link CubeFact},
 * and writes it to the {@link Cube} dataset identified by the name property.
 * <p/>
 * If {@link Cube} dataset does not exist, it will be created using properties provided with this sink. See more
 * information on available {@link Cube} dataset configuration properties at
 * io.cdap.cdap.data2.dataset2.lib.cube.CubeDatasetDefinition.
 * <p/>
 * To configure transformation from {@link StructuredRecord} to a {@link CubeFact} the
 * mapping configuration is required, following {@link StructuredRecordToCubeFact} documentation.
 */
// todo: add unit-test once CDAP-2156 is resolved
@Plugin(type = "batchsink")
@Name("Cube")
@Description("CDAP Cube Dataset Batch Sink")
@Requirements(datasetTypes = Cube.TYPE)
public class BatchCubeSink extends BatchWritableSink<StructuredRecord, byte[], CubeFact> {
  private final CubeSinkConfig config;

  public BatchCubeSink(CubeSinkConfig config) {
    super(config);
    this.config = config;
  }

  private StructuredRecordToCubeFact transform;

  @Override
  public void initialize(BatchRuntimeContext context) throws Exception {
    super.initialize(context);
    transform = new StructuredRecordToCubeFact(getProperties());
  }

  @VisibleForTesting
  @Override
  protected Map<String, String> getProperties() {
    Map<String, String> properties = new HashMap<>();
    properties.putAll(config.getProperties().getProperties());

    // add aggregations
    if (!Strings.isNullOrEmpty(config.getAggregations())) {
      properties.remove(Properties.Cube.AGGREGATIONS);
      properties.putAll(CubeUtils.parseAndGetProperties(Properties.Cube.AGGREGATIONS,
                                                        config.getAggregations(), ";", ":",
                                                        new Function<String, String>() {
                                                          @Override
                                                          public String apply(String input) {
                                                            return "dataset.cube.aggregation." + input + ".dimensions";
                                                          }
                                                        }));
    }

    // add measurements
    if (!Strings.isNullOrEmpty(config.getMeasurements())) {
      properties.remove(Properties.Cube.MEASUREMENTS);
      properties.putAll(CubeUtils.parseAndGetProperties(Properties.Cube.MEASUREMENTS,
                                                        config.getMeasurements(), ";", ":",
                                                        new Function<String, String>() {
                                                          @Override
                                                          public String apply(String input) {
                                                            return Properties.Cube.MEASUREMENT_PREFIX + input;
                                                          }
                                                        }));
    }
    properties.put(Properties.BatchReadableWritable.NAME, config.getName());
    properties.put(Properties.BatchReadableWritable.TYPE, Cube.class.getName());
    return properties;
  }

  @Override
  public void transform(StructuredRecord input, Emitter<KeyValue<byte[], CubeFact>> emitter) throws Exception {
    emitter.emit(new KeyValue<byte[], CubeFact>(null, transform.transform(input)));
  }
}
