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

package com.ami.ddframe.job.executor;

import com.ami.ddframe.job.executor.handler.ExecutorServiceHandlerRegistryTest;
import com.ami.ddframe.job.executor.handler.JobPropertiesTest;
import com.ami.ddframe.job.executor.handler.impl.DefaultJobExceptionHandlerTest;
import com.ami.ddframe.job.executor.type.DataflowJobExecutorTest;
import com.ami.ddframe.job.executor.type.ScriptJobExecutorTest;
import com.ami.ddframe.job.executor.type.SimpleJobExecutorTest;
import com.ami.ddframe.job.executor.type.WrongJobExecutorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        JobExecutorFactoryTest.class,
        ExecutorServiceHandlerRegistryTest.class, 
        JobPropertiesTest.class,
        DefaultJobExceptionHandlerTest.class, 
        SimpleJobExecutorTest.class,
        WrongJobExecutorTest.class,
        DataflowJobExecutorTest.class, 
        ScriptJobExecutorTest.class
    })
public final class AllExecutorTests {
}
