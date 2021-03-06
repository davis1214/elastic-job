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

package com.ami.ddframe.job.lite.spring.job;

import com.ami.ddframe.job.lite.internal.schedule.JobRegistry;
import com.ami.ddframe.job.lite.spring.fixture.job.ref.RefFooSimpleElasticJob;
import com.ami.ddframe.job.lite.spring.test.AbstractZookeeperJUnit4SpringContextTests;
import com.ami.ddframe.job.reg.base.CoordinatorRegistryCenter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

import static com.ami.ddframe.job.util.concurrent.BlockUtils.sleep;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = "classpath:META-INF/job/withJobRef.xml")
public final class JobSpringNamespaceWithRefTest extends AbstractZookeeperJUnit4SpringContextTests {
    
    private final String simpleJobName = "simpleElasticJob_job_ref";
    
    @Resource
    private CoordinatorRegistryCenter regCenter;
    
    @Before
    @After
    public void reset() {
        RefFooSimpleElasticJob.reset();
    }
    
    @After
    public void tearDown() {
        JobRegistry.getInstance().shutdown(simpleJobName);
    }
    
    @Test
    public void assertSpringJobBean() {
        assertSimpleElasticJobBean();
    }

    private void assertSimpleElasticJobBean() {
        while (!RefFooSimpleElasticJob.isCompleted()) {
            sleep(100L);
        }
        assertTrue(RefFooSimpleElasticJob.isCompleted());
        assertTrue(regCenter.isExisted("/" + simpleJobName + "/sharding"));
    }
}
