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

import com.ami.ddframe.job.config.JobCoreConfiguration;
import com.ami.ddframe.job.lite.internal.listener.AbstractJobListener;
import com.ami.ddframe.job.lite.internal.listener.AbstractListenerManager;
import com.ami.ddframe.job.lite.internal.schedule.JobRegistry;
import com.ami.ddframe.job.reg.base.CoordinatorRegistryCenter;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

/**
 * 重调度监听管理器.
 *
 * @author caohao
 * @author zhangliang
 */
public final class RescheduleListenerManager extends AbstractListenerManager {

    private final com.ami.ddframe.job.lite.internal.config.ConfigurationNode configNode;

    private final String jobName;

    public RescheduleListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
        super(regCenter, jobName);
        this.jobName = jobName;
        configNode = new com.ami.ddframe.job.lite.internal.config.ConfigurationNode(jobName);
    }

    @Override
    public void start() {
        addDataListener(new CronSettingAndJobEventChangedJobListener());
    }

    class CronSettingAndJobEventChangedJobListener extends AbstractJobListener {

        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (configNode.isConfigPath(path) && Type.NODE_UPDATED == eventType && !JobRegistry.getInstance().isShutdown(jobName)) {
                final JobCoreConfiguration coreConfig = com.ami.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactory.fromJson(data).getTypeConfig().getCoreConfig();
                JobRegistry.getInstance().getJobScheduleController(jobName).rescheduleJob(coreConfig.getCron(), coreConfig.getFixedRate());
            }
        }
    }
}
