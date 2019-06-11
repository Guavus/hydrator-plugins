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

package io.cdap.plugin.transform;

import com.google.common.base.Preconditions;
import io.cdap.cdap.etl.api.Arguments;
import io.cdap.cdap.etl.api.LookupConfig;
import io.cdap.cdap.etl.api.LookupProvider;
import io.cdap.cdap.etl.api.StageMetrics;
import io.cdap.plugin.common.script.JavaTypeConverters;
import io.cdap.plugin.common.script.ScriptContext;
import org.slf4j.Logger;

import java.util.Map;
import javax.annotation.Nullable;

/**
 * Context passed to {@link ValidatorTransform} script
 */
public class ValidatorScriptContext extends ScriptContext {
  private final Map<String, Object> validators;

  public ValidatorScriptContext(Logger logger, StageMetrics metrics, LookupProvider lookup,
                                @Nullable LookupConfig lookupConfig, JavaTypeConverters js,
                                Map<String, Object> validators, Arguments arguments) {
    super(logger, metrics, lookup, lookupConfig, js, arguments);
    this.validators = validators;
  }

  public Object getValidator(String validatorName) {
    Preconditions.checkArgument(validators.containsKey(validatorName),
                                String.format("Invalid validator name %s", validatorName));
    return validators.get(validatorName);
  }
}
