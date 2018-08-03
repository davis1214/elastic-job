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

package com.ami.ddframe.job;

import com.ami.ddframe.job.api.AllApiTests;
import com.ami.ddframe.job.config.AllConfigTests;
import com.ami.ddframe.job.context.AllContextTests;
import com.ami.ddframe.job.event.AllEventTests;
import com.ami.ddframe.job.exception.AllExceptionTests;
import com.ami.ddframe.job.executor.AllExecutorTests;
import com.ami.ddframe.job.reg.AllRegTests;
import com.ami.ddframe.job.statistics.AllStatisticsTests;
import com.ami.ddframe.job.util.AllUtilTests;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AllRegTests.class,
        AllContextTests.class,
        AllApiTests.class, 
        AllConfigTests.class, 
        AllExecutorTests.class, 
        AllEventTests.class, 
        AllExceptionTests.class,
        AllStatisticsTests.class,
        AllUtilTests.class
    })
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AllCoreTests {
}
