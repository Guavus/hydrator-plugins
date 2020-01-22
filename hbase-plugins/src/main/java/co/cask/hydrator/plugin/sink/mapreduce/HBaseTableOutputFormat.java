/*
 * Copyright © 2017 Cask Data, Inc.
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

package co.cask.hydrator.plugin.sink.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * A wrapper class around TableOutputFormat, that sets the current class's classloader as the classloader of the
 * Configuration object used by TableOutputFormat.
 *
 * @param <KEY> Type of Key
 */
public class HBaseTableOutputFormat<KEY> extends TableOutputFormat<KEY> {

  private static final Logger LOG = LoggerFactory.getLogger(HBaseTableOutputFormat.class);

  public static final String HBASE_CUSTOM_TABLENAME = "hbase.custom.tablename";
  public static final String HBASE_CUSTOM_COLUMNFAMILY = "hbase.custom.Columnfamily";

  @Override
  public void setConf(Configuration otherConf) {
    // To resolve CDAP-12731, set the current class's classloader to the thread's context classloader,
    // so that it gets picked up when the super.setConf() calls HBaseConfiguration.create(Configuration)
    ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    HBaseAdmin admin = null;
    try {
      super.setConf(otherConf);
      Configuration configuration = super.getConf();
      admin = new HBaseAdmin(configuration);
      createOrUpdateTable(configuration, admin);
    } catch (IOException e) {
      LOG.error("Error while creating hbase table ", e);
      throw new RuntimeException(e);
    } finally {
      Thread.currentThread().setContextClassLoader(originalClassLoader);
      try {
        if (admin != null) {
          admin.close();
        }
      } catch (IOException e) {
        LOG.warn("Error while closing hbase admin ", e);
      }
    }
  }

  private void createOrUpdateTable(Configuration conf, HBaseAdmin admin) throws IOException {
    String tableName = conf.get(HBASE_CUSTOM_TABLENAME);
    String columnFamily = conf.get(HBASE_CUSTOM_COLUMNFAMILY);
    if (admin.tableExists(tableName)) {
      try (HTable table = new HTable(conf, tableName)) {
        // check if column family exists
        admin.disableTable(table.getTableName());
        createFamilyIfNotExist(columnFamily, table, admin);
        admin.enableTable(table.getTableName());
      }
      LOG.info("table {} already exists", tableName);

    } else {
      HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
      tableDesc.addFamily(new HColumnDescriptor(columnFamily));
      admin.createTable(tableDesc);
      LOG.info("table has been created with name {}and with column family {}", tableName, columnFamily);
    }
  }


  private static void createFamilyIfNotExist(String family, HTable table, HBaseAdmin admin) {
    try {
      // check if column family exists
      boolean exists = false;
      for (HColumnDescriptor familyDescriptor : table.getTableDescriptor().getFamilies()) {
        if (Bytes.toString(familyDescriptor.getName()).equals(family)) {
          exists = true;
          break;
        }
      }
      // if not: add it
      if (!exists) {
        admin.addColumn(table.getTableName(), new HColumnDescriptor(family));
        LOG.info("column family {} updated in table {}", family, table.getName().getNameAsString());
      }
    } catch (IOException e) {
      LOG.error("error while adding column family", e);
      throw new RuntimeException(e);
    }
  }
}

