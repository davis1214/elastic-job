/*
 * Copyright 1999-2015 ami.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.ami.ddframe.job.cloud.executor.fixture;


import com.ami.ddframe.job.config.JobCoreConfiguration;
import com.ami.ddframe.job.config.JobRootConfiguration;
import com.ami.ddframe.job.config.JobTypeConfiguration;
import com.ami.ddframe.job.config.script.ScriptJobConfiguration;
import com.ami.ddframe.job.executor.handler.JobProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TestScriptJobConfiguration implements JobRootConfiguration {
    
    private final String scriptCommandLine;
    
    @Override
    public JobTypeConfiguration getTypeConfig() {
        return new ScriptJobConfiguration(JobCoreConfiguration.newBuilder("test_script_job", "0/1 * * * * ?", 3)
                .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), "ignoredExceptionHandler").build(), scriptCommandLine);
    }
}
