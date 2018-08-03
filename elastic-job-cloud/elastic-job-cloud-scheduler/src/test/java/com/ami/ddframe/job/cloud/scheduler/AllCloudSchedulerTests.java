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

package com.ami.ddframe.job.cloud.scheduler;

import com.ami.ddframe.job.cloud.scheduler.env.AllEnvTests;
import com.ami.ddframe.job.cloud.scheduler.config.AllConfigTests;
import com.ami.ddframe.job.cloud.scheduler.context.AllContextTests;
import com.ami.ddframe.job.cloud.scheduler.ha.AllHATests;
import com.ami.ddframe.job.cloud.scheduler.mesos.AllMesosTests;
import com.ami.ddframe.job.cloud.scheduler.producer.AllProducerTests;
import com.ami.ddframe.job.cloud.scheduler.restful.AllRestfulTests;
import com.ami.ddframe.job.cloud.scheduler.state.AllStateTests;
import com.ami.ddframe.job.cloud.scheduler.statistics.AllStatisticTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AllEnvTests.class, 
        AllContextTests.class, 
        AllConfigTests.class, 
        AllStateTests.class, 
        AllProducerTests.class, 
        AllRestfulTests.class, 
        AllMesosTests.class,
        AllStatisticTests.class,
        AllHATests.class
    })
public final class AllCloudSchedulerTests {
}
