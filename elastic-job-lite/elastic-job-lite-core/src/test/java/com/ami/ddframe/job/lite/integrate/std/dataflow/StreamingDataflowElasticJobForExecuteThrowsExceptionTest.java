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

package com.ami.ddframe.job.lite.integrate.std.dataflow;

import com.ami.ddframe.job.lite.config.LiteJobConfiguration;
import com.ami.ddframe.job.lite.integrate.AbstractBaseStdJobAutoInitTest;
import com.ami.ddframe.job.lite.integrate.WaitingUtils;
import com.ami.ddframe.job.lite.integrate.fixture.dataflow.StreamingDataflowElasticJobForExecuteThrowsException;
import com.ami.ddframe.job.lite.fixture.util.JobConfigurationUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public final class StreamingDataflowElasticJobForExecuteThrowsExceptionTest extends AbstractBaseStdJobAutoInitTest {
    
    public StreamingDataflowElasticJobForExecuteThrowsExceptionTest() {
        super(StreamingDataflowElasticJobForExecuteThrowsException.class);
    }
    
    @Before
    @After
    public void reset() {
        StreamingDataflowElasticJobForExecuteThrowsException.reset();
    }
    
    @Override
    protected void setLiteJobConfig(final LiteJobConfiguration liteJobConfig) {
        JobConfigurationUtil.setFieldValue(liteJobConfig.getTypeConfig(), "streamingProcess", true);
    }
    
    @Test
    public void assertJobInit() {
        while (!StreamingDataflowElasticJobForExecuteThrowsException.isCompleted()) {
            WaitingUtils.waitingShortTime();
        }
        assertTrue(getRegCenter().isExisted("/" + getJobName() + "/sharding"));
    }
}
