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

package com.ami.ddframe.job.cloud.executor.local;

import com.ami.ddframe.job.cloud.executor.local.fixture.TestDataflowJob;
import com.ami.ddframe.job.cloud.executor.local.fixture.TestSimpleJob;
import com.ami.ddframe.job.config.JobCoreConfiguration;
import com.ami.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.ami.ddframe.job.config.script.ScriptJobConfiguration;
import com.ami.ddframe.job.config.simple.SimpleJobConfiguration;
import com.ami.ddframe.job.exception.JobSystemException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public final class LocalTaskExecutorTest {
    
    @Before
    public void setUp() {
        TestSimpleJob.setShardingContext(null);
        TestDataflowJob.setInput(null);
        TestDataflowJob.setOutput(null);
    }
    
    @Test
    public void assertSimpleJob() {
        new LocalTaskExecutor(new LocalCloudJobConfiguration(new SimpleJobConfiguration(JobCoreConfiguration
                .newBuilder(TestSimpleJob.class.getSimpleName(), "*/2 * * * * ?", 3).build(), TestSimpleJob.class.getName()), 1)).execute();
        assertThat(TestSimpleJob.getShardingContext().getJobName(), is(TestSimpleJob.class.getSimpleName()));
        assertThat(TestSimpleJob.getShardingContext().getShardingItem(), is(1));
        assertThat(TestSimpleJob.getShardingContext().getShardingTotalCount(), is(3));
        assertThat(TestSimpleJob.getShardingContext().getShardingItem(), is(1));
        assertNull(TestSimpleJob.getShardingContext().getShardingParameter());
        assertThat(TestSimpleJob.getShardingContext().getJobParameter(), is(""));
    }
    
    @Test
    public void assertSpringSimpleJob() {
        new LocalTaskExecutor(new LocalCloudJobConfiguration(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder(
                TestSimpleJob.class.getSimpleName(), "*/2 * * * * ?", 3).shardingItemParameters("0=Beijing,1=Shanghai,2=Guangzhou").jobParameter("dbName=ami").build(),
                TestSimpleJob.class.getName()), 1, "testSimpleJob", "applicationContext.xml")).execute();
        assertThat(TestSimpleJob.getShardingContext().getJobName(), is(TestSimpleJob.class.getSimpleName()));
        assertThat(TestSimpleJob.getShardingContext().getShardingTotalCount(), is(3));
        assertThat(TestSimpleJob.getShardingContext().getJobParameter(), is("dbName=ami"));
        assertThat(TestSimpleJob.getShardingContext().getShardingItem(), is(1));
        assertThat(TestSimpleJob.getShardingParameters().size(), is(1));
        assertThat(TestSimpleJob.getShardingParameters().iterator().next(), is("Shanghai"));
    }
    
    @Test
    public void assertDataflowJob() {
        TestDataflowJob.setInput(Arrays.asList("1", "2", "3"));
        new LocalTaskExecutor(new LocalCloudJobConfiguration(new DataflowJobConfiguration(JobCoreConfiguration
                .newBuilder(TestDataflowJob.class.getSimpleName(), "*/2 * * * * ?", 10).build(), TestDataflowJob.class.getName(), false), 5)).execute();
        assertFalse(TestDataflowJob.getOutput().isEmpty());
        for (String each : TestDataflowJob.getOutput()) {
            assertTrue(each.endsWith("-d"));
        }
    }
    
    @Test
    public void assertScriptJob() throws IOException {
        new LocalTaskExecutor(new LocalCloudJobConfiguration(
                new ScriptJobConfiguration(JobCoreConfiguration.newBuilder("TestScriptJob", "*/2 * * * * ?", 3).build(), buildScriptCommandLine()), 1)).execute();
    }
    
    private static String buildScriptCommandLine() throws IOException {
        if (System.getProperties().getProperty("os.name").contains("Windows")) {
            return Paths.get(LocalTaskExecutorTest.class.getResource("/script/TestScriptJob.bat").getPath().substring(1)).toString();
        }
        Path result = Paths.get(LocalTaskExecutorTest.class.getResource("/script/TestScriptJob.sh").getPath());
        Files.setPosixFilePermissions(result, PosixFilePermissions.fromString("rwxr-xr-x"));
        return result.toString();
    }
    
    @Test(expected = JobSystemException.class)
    public void assertNotExistsJobClass() {
        new LocalTaskExecutor(new LocalCloudJobConfiguration(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("not exist", "*/2 * * * * ?", 3).build(), "not exist"), 1)).execute();
    }
}
