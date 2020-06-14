/*
 * Copyright © 2015-2018 Cask Data, Inc.
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

package co.cask.hydrator.plugin.batch.source;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.common.Bytes;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.api.dataset.DatasetProperties;
import co.cask.cdap.api.dataset.lib.KeyValueTable;
import co.cask.cdap.api.plugin.EndpointPluginContext;
import co.cask.cdap.etl.api.batch.BatchSource;
import co.cask.cdap.etl.api.batch.BatchSourceContext;
import co.cask.hydrator.format.FileFormat;
import co.cask.hydrator.format.input.PathTrackingInputFormat;
import co.cask.hydrator.format.input.TextInputProvider;
import co.cask.hydrator.format.plugin.AbstractFileSource;
import co.cask.hydrator.plugin.common.sftp.SFTPFileSystem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static co.cask.hydrator.plugin.common.sftp.SFTPFileSystem.*;


/**
 * A {@link BatchSource} to use any distributed file system as a Source.
 */
@Plugin(type = "batchsource")
@Name("File")
@Description("Batch source for File Systems")
public class FileBatchSource extends AbstractFileSource<FileSourceConfig> {
  public static final Schema DEFAULT_SCHEMA = TextInputProvider.getDefaultSchema(null);
  static final String INPUT_NAME_CONFIG = "input.path.name";
  static final String INPUT_REGEX_CONFIG = "input.path.regex";
  static final String LAST_TIME_READ = "last.time.read";
  static final String CUTOFF_READ_TIME = "cutoff.read.time";
  static final String USE_TIMEFILTER = "timefilter";
  private static final Gson GSON = new Gson();
  private static final Type ARRAYLIST_DATE_TYPE = new TypeToken<ArrayList<Date>>() { }.getType();
  private final FileSourceConfig config;

  public FileBatchSource(FileSourceConfig config) {
    super(config);
    this.config = config;
  }

  @Override
  public void prepareRun(BatchSourceContext context) throws Exception {
    super.prepareRun(context);

    // Need to create dataset now if macro was provided at configure time
    if (config.getTimeTable() != null && !context.datasetExists(config.getTimeTable())) {
      context.createDataset(config.getTimeTable(), KeyValueTable.class.getName(), DatasetProperties.EMPTY);
    }
  }

  /**
   * Endpoint method to get the output schema of a source.
   *
   * @param config configuration for the source
   * @param pluginContext context to create plugins
   * @return schema of fields
   */
  @Path("getSchema")
  public Schema getSchema(FileSourceConfig config, EndpointPluginContext pluginContext) {
    FileFormat fileFormat = config.getFormat();
    if (fileFormat == null) {
      return config.getSchema();
    }
    Schema schema = fileFormat.getSchema(config.getPathField(), config.getPath());
    return schema == null ? config.getSchema() : schema;
  }

  @Override
  protected Map<String, String> getFileSystemProperties(BatchSourceContext context) {
    Map<String, String> properties = new HashMap<>(config.getFileSystemProperties());
    if (config.shouldCopyHeader()) {
      properties.put(PathTrackingInputFormat.COPY_HEADER, "true");
    }

    // TODO:(CDAP-14424) Remove time table logic
    // everything from this point on should be removed in a future release.
    // the time table stuff is super specific, requiring input paths to be in a very specific format
    // and it assumes the pipeline is scheduled to run in a specific way

    //SimpleDateFormat needs to be local because it is not threadsafe
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");

    //calculate date one hour ago, rounded down to the nearest hour
    Date prevHour = new Date(context.getLogicalStartTime() - TimeUnit.HOURS.toMillis(1));
    Calendar cal = Calendar.getInstance();
    cal.setTime(prevHour);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    prevHour = cal.getTime();

    if (config.getTimeTable() != null) {
      KeyValueTable table = context.getDataset(config.getTimeTable());
      String datesToRead = Bytes.toString(table.read(LAST_TIME_READ));
      if (datesToRead == null) {
        List<Date> firstRun = new ArrayList<>(1);
        firstRun.add(new Date(0));
        datesToRead = GSON.toJson(firstRun, ARRAYLIST_DATE_TYPE);
      }
      List<Date> attempted = new ArrayList<>();
      attempted.add(prevHour);
      String updatedDatesToRead = GSON.toJson(attempted, ARRAYLIST_DATE_TYPE);
      if (!updatedDatesToRead.equals(datesToRead)) {
        table.write(LAST_TIME_READ, updatedDatesToRead);
      }
      properties.put(LAST_TIME_READ, datesToRead);
    }

    properties.put(CUTOFF_READ_TIME, dateFormat.format(prevHour));
    Pattern pattern = config.getFilePattern();
    properties.put(INPUT_REGEX_CONFIG, pattern == null ? ".*" : pattern.toString());
    properties.put("mapreduce.input.pathFilter.class", BatchFileFilter.class.getName());

    return properties;
  }

  @Override
  public org.apache.hadoop.fs.Path getSourcePath(Configuration conf) {
    return (isSourceSftp() ? SFTPFileSystem.getPath(conf) : super.getSourcePath(conf));
  }

  @Override
  public FileSystem getFileSystem(URI uri, Configuration conf) throws IOException {
    if(isSourceSftp()) {
      FileSystem fs = new SFTPFileSystem();
      try {
        fs.initialize(uri, conf);
      } catch (IOException ex) {
        throw new RuntimeException("Unable to initialize SFTP connection.", ex);
      }
      return fs;
    } else return super.getFileSystem(uri, conf);
  }

  @Override
  public void addAdditionalConfigurations(Configuration conf) {
    if(isSourceSftp()) {
      this.addSFTPConfigurations(conf, config.getHost(), config.getPort(), config.getPath(), config.getUsername(), config.getPassword());
    }
  }

  public void addSFTPConfigurations(Configuration conf, String host, int port, String path, String username, String password) {
    // validations
    if(host == null || host.trim().equals("")) {
      throw new IllegalArgumentException("Sftp host can not be empty.");
    }

    if(username == null || username.trim().equals("")) {
      throw new IllegalArgumentException("Username can not be empty.");
    }

    if(password == null || password.equals("")) {
      throw new IllegalArgumentException("Password can not be empty.");
    }

    if(path == null || path.equals("")) {
      throw new IllegalArgumentException("Sftp path can not be empty.");
    }


    conf.set(SFTP_IMPL_KEY, "co.cask.hydrator.plugin.common.sftp.SFTPFileSystem");
    conf.set(SFTP_PATH_TO_READ, path.trim());

    // Limit the number of splits to 1 since FTPInputStream does not support seek;
    conf.set(FileInputFormat.SPLIT_MINSIZE, Long.toString(Long.MAX_VALUE));

    conf.set(PARAM_HOST, host.trim());
    conf.set(PARAM_PORT, Integer.toString(port));

    conf.set(PARAM_USER, username.trim());
    conf.set(PARAM_PASSWORD, password);
  }

  /**
   * Check if the path is present on sftp
   * @return
   */
  public boolean isSourceSftp() {
    return (config.getHost() != null && !config.getHost().trim().equals(""));
  }
}
