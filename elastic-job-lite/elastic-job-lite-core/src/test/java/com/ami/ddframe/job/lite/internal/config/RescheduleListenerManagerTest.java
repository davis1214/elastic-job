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

package com.ami.ddframe.job.lite.internal.config;

import com.ami.ddframe.job.event.JobEventBus;
import com.ami.ddframe.job.lite.api.strategy.JobInstance;
import com.ami.ddframe.job.lite.fixture.LiteJsonConstants;
import com.ami.ddframe.job.lite.internal.config.RescheduleListenerManager.CronSettingAndJobEventChangedJobListener;
import com.ami.ddframe.job.lite.internal.schedule.JobRegistry;
import com.ami.ddframe.job.lite.internal.schedule.JobScheduleController;
import com.ami.ddframe.job.lite.internal.storage.JobNodeStorage;
import com.ami.ddframe.job.reg.base.CoordinatorRegistryCenter;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public final class RescheduleListenerManagerTest {

    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobEventBus jobEventBus;

    private final RescheduleListenerManager rescheduleListenerManager = new RescheduleListenerManager(null, "test_job");

    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        ReflectionUtils.setFieldValue(rescheduleListenerManager, rescheduleListenerManager.getClass().getSuperclass().getDeclaredField("jobNodeStorage"), jobNodeStorage);
    }

    @Test
    public void assertStart() {
        rescheduleListenerManager.start();
        verify(jobNodeStorage).addDataListener(ArgumentMatchers.<CronSettingAndJobEventChangedJobListener>any());
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsNotCronPath() {
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config/other", Type.NODE_ADDED, LiteJsonConstants.getJobJson());
        verify(jobScheduleController, times(0)).rescheduleJob(ArgumentMatchers.<String>any(), 0);
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsCronPathButNotUpdate() {
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config", Type.NODE_ADDED, LiteJsonConstants.getJobJson());
        verify(jobScheduleController, times(0)).rescheduleJob(ArgumentMatchers.<String>any(), 0);
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsCronPathAndUpdateButCannotFindJob() {
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config", Type.NODE_UPDATED, LiteJsonConstants.getJobJson());
        verify(jobScheduleController, times(0)).rescheduleJob(ArgumentMatchers.<String>any(), 0);
    }

    @Test
    public void assertCronSettingChangedJobListenerWhenIsCronPathAndUpdateAndFindJob() {
        JobRegistry.getInstance().addJobInstance("test_job", new JobInstance("127.0.0.1@-@0"));
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        rescheduleListenerManager.new CronSettingAndJobEventChangedJobListener().dataChanged("/test_job/config", Type.NODE_UPDATED, LiteJsonConstants.getJobJson());
        verify(jobScheduleController).rescheduleJob("0/1 * * * * ?", 0);
        JobRegistry.getInstance().shutdown("test_job");
    }
}
