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

package com.ami.ddframe.job.lite.internal;

import com.ami.ddframe.job.lite.internal.config.ConfigurationNodeTest;
import com.ami.ddframe.job.lite.internal.config.ConfigurationServiceTest;
import com.ami.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactoryTest;
import com.ami.ddframe.job.lite.internal.config.RescheduleListenerManagerTest;
import com.ami.ddframe.job.lite.internal.election.ElectionListenerManagerTest;
import com.ami.ddframe.job.lite.internal.election.LeaderNodeTest;
import com.ami.ddframe.job.lite.internal.election.LeaderServiceTest;
import com.ami.ddframe.job.lite.internal.failover.FailoverListenerManagerTest;
import com.ami.ddframe.job.lite.internal.failover.FailoverNodeTest;
import com.ami.ddframe.job.lite.internal.failover.FailoverServiceTest;
import com.ami.ddframe.job.lite.internal.guarantee.GuaranteeNodeTest;
import com.ami.ddframe.job.lite.internal.guarantee.GuaranteeServiceTest;
import com.ami.ddframe.job.lite.internal.instance.InstanceNodeTest;
import com.ami.ddframe.job.lite.internal.instance.InstanceServiceTest;
import com.ami.ddframe.job.lite.internal.instance.ShutdownListenerManagerTest;
import com.ami.ddframe.job.lite.internal.instance.TriggerListenerManagerTest;
import com.ami.ddframe.job.lite.internal.listener.JobListenerTest;
import com.ami.ddframe.job.lite.internal.listener.ListenerManagerTest;
import com.ami.ddframe.job.lite.internal.listener.RegistryCenterConnectionStateListenerTest;
import com.ami.ddframe.job.lite.internal.monitor.MonitorServiceDisableTest;
import com.ami.ddframe.job.lite.internal.monitor.MonitorServiceEnableTest;
import com.ami.ddframe.job.lite.internal.reconcile.ReconcileServiceTest;
import com.ami.ddframe.job.lite.internal.schedule.JobRegistryTest;
import com.ami.ddframe.job.lite.internal.schedule.JobScheduleControllerTest;
import com.ami.ddframe.job.lite.internal.schedule.JobTriggerListenerTest;
import com.ami.ddframe.job.lite.internal.schedule.LiteJobFacadeTest;
import com.ami.ddframe.job.lite.internal.schedule.SchedulerFacadeTest;
import com.ami.ddframe.job.lite.internal.server.ServerNodeTest;
import com.ami.ddframe.job.lite.internal.server.ServerServiceTest;
import com.ami.ddframe.job.lite.internal.sharding.ExecutionContextServiceTest;
import com.ami.ddframe.job.lite.internal.sharding.ExecutionServiceTest;
import com.ami.ddframe.job.lite.internal.sharding.MonitorExecutionListenerManagerTest;
import com.ami.ddframe.job.lite.internal.sharding.ShardingListenerManagerTest;
import com.ami.ddframe.job.lite.internal.sharding.ShardingNodeTest;
import com.ami.ddframe.job.lite.internal.sharding.ShardingServiceTest;
import com.ami.ddframe.job.lite.internal.storage.JobNodePathTest;
import com.ami.ddframe.job.lite.internal.storage.JobNodeStorageTest;
import com.ami.ddframe.job.lite.internal.util.SensitiveInfoUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        JobNodePathTest.class,
        JobNodeStorageTest.class,
        SensitiveInfoUtilsTest.class,
        ConfigurationServiceTest.class, 
        ConfigurationNodeTest.class,
        RescheduleListenerManagerTest.class,
        LiteJobConfigurationGsonFactoryTest.class, 
        LeaderServiceTest.class, 
        LeaderNodeTest.class,
        ElectionListenerManagerTest.class, 
        ServerServiceTest.class, 
        InstanceNodeTest.class,
        InstanceServiceTest.class,
        ShutdownListenerManagerTest.class,
        TriggerListenerManagerTest.class,
        ShardingServiceTest.class, 
        ServerNodeTest.class,
        ShardingListenerManagerTest.class, 
        ExecutionContextServiceTest.class, 
        ExecutionServiceTest.class,
        MonitorExecutionListenerManagerTest.class, 
        ShardingNodeTest.class,
        FailoverServiceTest.class, 
        FailoverNodeTest.class,
        FailoverListenerManagerTest.class, 
        JobRegistryTest.class,
        JobScheduleControllerTest.class,
        JobTriggerListenerTest.class, 
        ListenerManagerTest.class, 
        JobListenerTest.class,
        MonitorServiceEnableTest.class,
        MonitorServiceDisableTest.class, 
        GuaranteeNodeTest.class,
        GuaranteeServiceTest.class, 
        SchedulerFacadeTest.class,
        LiteJobFacadeTest.class, 
        ReconcileServiceTest.class,
        RegistryCenterConnectionStateListenerTest.class
    })
public final class AllInternalTests {
}
