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

package com.ami.ddframe.job.cloud.scheduler.producer;

import org.quartz.JobKey;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 瞬时作业生成器数据访问对象.
 *
 * @author caohao
 * @author zhangliang
 */
final class TransientProducerRepository {
    
    private final ConcurrentHashMap<JobKey, List<String>> cronTasks = new ConcurrentHashMap<>(256, 1);
    
    synchronized void put(final JobKey jobKey, final String jobName) {
        remove(jobName);
        List<String> taskList = cronTasks.get(jobKey);
        if (null == taskList) {
            taskList = new CopyOnWriteArrayList<>();
            taskList.add(jobName);
            cronTasks.put(jobKey, taskList);
            return;
        }
        if (!taskList.contains(jobName)) {
            taskList.add(jobName);
        }
    }
    
    synchronized void remove(final String jobName) {
        for (Entry<JobKey, List<String>> each : cronTasks.entrySet()) {
            JobKey jobKey = each.getKey();
            List<String> jobNames = each.getValue();
            jobNames.remove(jobName);
            if (jobNames.isEmpty()) {
                cronTasks.remove(jobKey);
            }
        }
    }
    
    List<String> get(final JobKey jobKey) {
        List<String> result = cronTasks.get(jobKey);
        return null == result ? Collections.<String>emptyList() : result;
    }
    
    boolean containsKey(final JobKey jobKey) {
        return cronTasks.containsKey(jobKey);
    }
    
    void removeAll() {
        cronTasks.clear();
    }
}
