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

package com.ami.ddframe.job.lite.api;

import com.google.common.base.Optional;
import com.ami.ddframe.job.api.ElasticJob;
import com.ami.ddframe.job.api.reflect.ReflectJob;
import com.ami.ddframe.job.api.script.ScriptJob;
import com.ami.ddframe.job.config.JobCoreConfiguration;
import com.ami.ddframe.job.config.JobTypeConfiguration;
import com.ami.ddframe.job.config.reflect.ReflectJobCofiguration;
import com.ami.ddframe.job.event.JobEventBus;
import com.ami.ddframe.job.event.JobEventConfiguration;
import com.ami.ddframe.job.exception.JobConfigurationException;
import com.ami.ddframe.job.exception.JobSystemException;
import com.ami.ddframe.job.executor.JobFacade;
import com.ami.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import com.ami.ddframe.job.lite.api.listener.ElasticJobListener;
import com.ami.ddframe.job.lite.api.strategy.JobInstance;
import com.ami.ddframe.job.lite.config.LiteJobConfiguration;
import com.ami.ddframe.job.lite.internal.guarantee.GuaranteeService;
import com.ami.ddframe.job.lite.internal.schedule.*;
import com.ami.ddframe.job.reg.base.CoordinatorRegistryCenter;
import lombok.Getter;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 作业调度器.
 *
 * @author zhangliang
 * @author caohao
 */
public class JobScheduler {
    private final Logger log = LoggerFactory.getLogger(JobScheduler.class);

    public static final String ELASTIC_JOB_DATA_MAP_KEY = "elasticJob";

    private static final String JOB_FACADE_DATA_MAP_KEY = "jobFacade";

    private final LiteJobConfiguration liteJobConfig;

    private final CoordinatorRegistryCenter regCenter;

    private JobDetail jobDetail;

    @Getter
    private final SchedulerFacade schedulerFacade;

    private final JobFacade jobFacade;

    public JobScheduler(final CoordinatorRegistryCenter regCenter, final LiteJobConfiguration liteJobConfig, final ElasticJobListener... elasticJobListeners) {
        this(regCenter, liteJobConfig, new JobEventBus(), null, elasticJobListeners);
    }

    public JobScheduler(final CoordinatorRegistryCenter regCenter, final LiteJobConfiguration liteJobConfig, final JobEventConfiguration jobEventConfig,
                        final ElasticJobListener... elasticJobListeners) {
        this(regCenter, liteJobConfig, new JobEventBus(jobEventConfig), null, elasticJobListeners);
    }

    public JobScheduler(final CoordinatorRegistryCenter regCenter, final LiteJobConfiguration liteJobConfig, final JobEventConfiguration jobEventConfig,
                        final JobDetail jobDetail, final ElasticJobListener... elasticJobListeners) {
        this(regCenter, liteJobConfig, new JobEventBus(jobEventConfig), jobDetail, elasticJobListeners);
    }

    public JobScheduler(final CoordinatorRegistryCenter regCenter, final LiteJobConfiguration liteJobConfig,
                        final JobDetail jobDetail, final ElasticJobListener... elasticJobListeners) {
        this(regCenter, liteJobConfig, new JobEventBus(), jobDetail, elasticJobListeners);
    }

    private JobScheduler(final CoordinatorRegistryCenter regCenter, final LiteJobConfiguration liteJobConfig, final JobEventBus jobEventBus,
                         final JobDetail jobDetail, final ElasticJobListener... elasticJobListeners) {
        JobRegistry.getInstance().addJobInstance(liteJobConfig.getJobName(), new JobInstance());
        this.liteJobConfig = liteJobConfig;
        this.regCenter = regCenter;
        this.jobDetail = jobDetail;
        List<ElasticJobListener> elasticJobListenerList = Arrays.asList(elasticJobListeners);
        setGuaranteeServiceForElasticJobListeners(regCenter, elasticJobListenerList);

        schedulerFacade = new SchedulerFacade(regCenter, liteJobConfig.getJobName(), elasticJobListenerList);

        //分片相关处理
        jobFacade = new LiteJobFacade(regCenter, liteJobConfig.getJobName(), Arrays.asList(elasticJobListeners), jobEventBus);
    }

    private void setGuaranteeServiceForElasticJobListeners(final CoordinatorRegistryCenter regCenter, final List<ElasticJobListener> elasticJobListeners) {
        GuaranteeService guaranteeService = new GuaranteeService(regCenter, liteJobConfig.getJobName());
        for (ElasticJobListener each : elasticJobListeners) {
            if (each instanceof AbstractDistributeOnceElasticJobListener) {
                ((AbstractDistributeOnceElasticJobListener) each).setGuaranteeService(guaranteeService);
            }
        }
    }

    /**
     * 初始化作业.
     */
    public void init() {
        // 1、注册配置下信息
        LiteJobConfiguration liteJobConfigFromRegCenter = schedulerFacade.updateJobConfiguration(liteJobConfig);

        final JobCoreConfiguration coreConfig = liteJobConfigFromRegCenter.getTypeConfig().getCoreConfig();

        //2、生成JobRegistry (分片总数)
        JobRegistry.getInstance().setCurrentShardingTotalCount(liteJobConfigFromRegCenter.getJobName(),
                coreConfig.getShardingTotalCount());

        //3、创建任务
        if (jobDetail == null) {
            jobDetail = createJobDetail(liteJobConfigFromRegCenter.getTypeConfig());
        } else {
            jobDetail.getJobDataMap().put(JOB_FACADE_DATA_MAP_KEY, jobFacade);
        }

        JobScheduleController jobScheduleController = new JobScheduleController(createScheduler(), jobDetail,
                liteJobConfigFromRegCenter.getJobName());

        //4、增加注册信息
        JobRegistry.getInstance().registerJob(liteJobConfigFromRegCenter.getJobName(), jobScheduleController, regCenter);

        //5、
        schedulerFacade.registerStartUpInfo(!liteJobConfigFromRegCenter.isDisabled());

        //6、
        jobScheduleController.scheduleJob(coreConfig.getCron(), coreConfig.getFixedRate());
    }

    public void init(final JobDetail jobDetail) {
        final Scheduler scheduler = createScheduler();
        init(scheduler, jobDetail);
    }

    public void init(final Scheduler scheduler, final JobDetail jobDetail) {
        // 1、注册配置下信息
        LiteJobConfiguration liteJobConfigFromRegCenter = schedulerFacade.updateJobConfiguration(liteJobConfig);

        final JobCoreConfiguration coreConfig = liteJobConfigFromRegCenter.getTypeConfig().getCoreConfig();
        //2、生成JobRegistry (分片总数)
        JobRegistry.getInstance().setCurrentShardingTotalCount(liteJobConfigFromRegCenter.getJobName(),
                coreConfig.getShardingTotalCount());

        JobScheduleController jobScheduleController = new JobScheduleController(scheduler, jobDetail,
                liteJobConfigFromRegCenter.getJobName());

        JobRegistry.getInstance().registerJob(liteJobConfigFromRegCenter.getJobName(), jobScheduleController, regCenter);

        //5、
        schedulerFacade.registerStartUpInfo(!liteJobConfigFromRegCenter.isDisabled());

        //6、
        jobScheduleController.scheduleJob(coreConfig.getCron(), coreConfig.getFixedRate());
    }

    private JobDetail createJobDetail(JobTypeConfiguration typeConfig) {
        final String jobClass = typeConfig.getJobClass();

        JobDetail result = JobBuilder.newJob(LiteJob.class).withIdentity(liteJobConfig.getJobName()).build();
        result.getJobDataMap().put(JOB_FACADE_DATA_MAP_KEY, jobFacade);
        Optional<ElasticJob> elasticJobInstance = createElasticJobInstance();
        if (elasticJobInstance.isPresent()) {
            result.getJobDataMap().put(ELASTIC_JOB_DATA_MAP_KEY, elasticJobInstance.get());
        } else if (jobClass.equals(ReflectJob.class.getCanonicalName())) {
            try {
                final ReflectJobCofiguration reflectJobCofig = (ReflectJobCofiguration) typeConfig;
                ReflectJob reflectionJob = new ReflectJob(reflectJobCofig.getMethod(), reflectJobCofig.getInstance());
                result.getJobDataMap().put(ELASTIC_JOB_DATA_MAP_KEY, reflectionJob);
            } catch (Exception ex) {
                throw new JobConfigurationException("Elastic-Job: Job class '%s' can not initialize.", jobClass);
            }
        } else if (!jobClass.equals(ScriptJob.class.getCanonicalName())) {
            try {
                result.getJobDataMap().put(ELASTIC_JOB_DATA_MAP_KEY, Class.forName(jobClass).newInstance());
            } catch (final ReflectiveOperationException ex) {
                throw new JobConfigurationException("Elastic-Job: Job class '%s' can not initialize.", jobClass);
            }
        }
        return result;
    }

    private JobDetail createJobDetail(final String jobClass) {
        JobDetail result = JobBuilder.newJob(LiteJob.class).withIdentity(liteJobConfig.getJobName()).build();
        result.getJobDataMap().put(JOB_FACADE_DATA_MAP_KEY, jobFacade);
        Optional<ElasticJob> elasticJobInstance = createElasticJobInstance();
        if (elasticJobInstance.isPresent()) {
            result.getJobDataMap().put(ELASTIC_JOB_DATA_MAP_KEY, elasticJobInstance.get());
        } else if (jobClass.equals(ReflectJob.class.getCanonicalName())) {
            try {
                //final Object value = Class.forName(jobClass).newInstance();
                ReflectJob reflectionJob = new ReflectJob();
                result.getJobDataMap().put(ELASTIC_JOB_DATA_MAP_KEY, reflectionJob);
            } catch (Exception ex) {
                throw new JobConfigurationException("Elastic-Job: Job class '%s' can not initialize.", jobClass);
            }
        } else if (!jobClass.equals(ScriptJob.class.getCanonicalName())) {
            try {
                final Object value = Class.forName(jobClass).newInstance();

                result.getJobDataMap().put(ELASTIC_JOB_DATA_MAP_KEY, value);
            } catch (final ReflectiveOperationException ex) {
                throw new JobConfigurationException("Elastic-Job: Job class '%s' can not initialize.", jobClass);
            }
        }
        return result;
    }


    protected Optional<ElasticJob> createElasticJobInstance() {
        return Optional.absent();
    }

    private Scheduler createScheduler() {
        Scheduler result;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            factory.initialize(getBaseQuartzProperties());
            result = factory.getScheduler();
            result.getListenerManager().addTriggerListener(schedulerFacade.newJobTriggerListener());
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
        return result;
    }

    private Properties getBaseQuartzProperties() {
        Properties result = new Properties();
        result.put("org.quartz.threadPool.class", org.quartz.simpl.SimpleThreadPool.class.getName());
        result.put("org.quartz.threadPool.threadCount", "1");
        result.put("org.quartz.scheduler.instanceName", liteJobConfig.getJobName());
        result.put("org.quartz.jobStore.misfireThreshold", "1");
        result.put("org.quartz.plugin.shutdownhook.class", JobShutdownHookPlugin.class.getName());
        result.put("org.quartz.plugin.shutdownhook.cleanShutdown", Boolean.TRUE.toString());
        return result;
    }
}
