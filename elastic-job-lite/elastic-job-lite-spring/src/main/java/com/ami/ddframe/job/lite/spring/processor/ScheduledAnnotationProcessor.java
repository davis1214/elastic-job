package com.ami.ddframe.job.lite.spring.processor;

import com.google.common.base.Strings;
import com.ami.ddframe.job.api.reflect.ReflectJob;
import com.ami.ddframe.job.config.JobCoreConfiguration;
import com.ami.ddframe.job.config.reflect.ReflectJobCofiguration;
import com.ami.ddframe.job.event.JobEventConfiguration;
import com.ami.ddframe.job.exception.JobConfigurationException;
import com.ami.ddframe.job.lite.api.JobScheduler;
import com.ami.ddframe.job.lite.config.LiteJobConfiguration;
import com.ami.ddframe.job.lite.internal.schedule.LiteJob;
import com.ami.ddframe.job.lite.spring.annotation.ElasticScheduler;
import com.ami.ddframe.job.lite.spring.annotation.ElasticSchedulers;
import com.ami.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param
 * @Author: DaviHe
 * @Description:
 * @Date: Created in 2018/6/13
 */
@Slf4j
public class ScheduledAnnotationProcessor implements BeanPostProcessor {

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    private final String ELASTIC_JOB_DATA_MAP_KEY = "elasticJob";

    private ZookeeperRegistryCenter regCenter;

    private JobEventConfiguration jobEventConfiguration;

    private String jobShardingStrategyClassName;

    private boolean shouldOverwriteliteJobConfig;

    public ScheduledAnnotationProcessor(ZookeeperRegistryCenter regCenter) {
        this.regCenter = regCenter;
    }

    public ScheduledAnnotationProcessor(ZookeeperRegistryCenter regCenter, JobEventConfiguration jobEventConfiguration, boolean shouldOverwriteliteJobConfig) {
        this.regCenter = regCenter;
        this.jobEventConfiguration = jobEventConfiguration;
        this.shouldOverwriteliteJobConfig = shouldOverwriteliteJobConfig;
    }

    public ScheduledAnnotationProcessor(ZookeeperRegistryCenter regCenter, String jobShardingStrategyClassName, boolean shouldOverwriteliteJobConfig) {
        this.regCenter = regCenter;
        this.jobShardingStrategyClassName = jobShardingStrategyClassName;
        this.shouldOverwriteliteJobConfig = shouldOverwriteliteJobConfig;
    }

    public ScheduledAnnotationProcessor(ZookeeperRegistryCenter regCenter, JobEventConfiguration jobEventConfiguration, String jobShardingStrategyClassName,
                                        boolean shouldOverwriteliteJobConfig) {
        this.regCenter = regCenter;
        this.jobEventConfiguration = jobEventConfiguration;
        this.jobShardingStrategyClassName = jobShardingStrategyClassName;
        this.shouldOverwriteliteJobConfig = shouldOverwriteliteJobConfig;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {

            Map<Method, Set<ElasticScheduler>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<Set<ElasticScheduler>>) method -> {
                        Set<ElasticScheduler> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(
                                method, ElasticScheduler.class, ElasticSchedulers.class);
                        return (!scheduledMethods.isEmpty() ? scheduledMethods : null);
                    });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (log.isTraceEnabled()) {
                    log.trace("No @Scheduled annotations found on bean class: " + bean.getClass());
                }
            } else {
                annotatedMethods.forEach((method, scheduledMethods) ->
                        scheduledMethods.forEach(scheduled -> processScheduled(scheduled, method, bean)));
                if (log.isDebugEnabled()) {
                    log.debug(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName +
                            "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    private void processScheduled(ElasticScheduler scheduler, Method method, Object bean) {
        log.info("processScheduled method {} , bean {} , cron {} ,total {}  ,param {} ", method.getName(), bean.toString(), scheduler.cron(), scheduler.shardingTotalCount(),
                scheduler.shardingItemParameters());

        String jobName = scheduler.jobName();
        if (StringUtils.isEmpty(jobName)) {
            jobName = method.getName();
        }

        JobCoreConfiguration coreConfig = null;

        if (!StringUtils.isEmpty(scheduler.cron())) {
            coreConfig = JobCoreConfiguration
                    .newBuilder(jobName, scheduler.cron(), scheduler.shardingTotalCount())
                    .shardingItemParameters(scheduler.shardingItemParameters())
                    .build();
        } else {
            coreConfig = JobCoreConfiguration
                    .newBuilder(jobName, scheduler.fixedRate(), scheduler.shardingTotalCount())
                    .shardingItemParameters(scheduler.shardingItemParameters())
                    .build();
        }

        ReflectJobCofiguration reflectJobCofig = new ReflectJobCofiguration(coreConfig,
                ReflectJob.class.getCanonicalName(), bean.getClass().getCanonicalName(), method.getName());

        final JobDetail jobDetail = createJobDetail(jobName, method, bean);

        final LiteJobConfiguration.Builder builder = LiteJobConfiguration.newBuilder(reflectJobCofig)
                .overwrite(shouldOverwriteliteJobConfig);

        if (!Strings.isNullOrEmpty(jobShardingStrategyClassName)) {
            builder.jobShardingStrategyClass(jobShardingStrategyClassName);
        }

        if (jobEventConfiguration != null) {
            new JobScheduler(regCenter, builder.build(), jobEventConfiguration, jobDetail).init();
            return;
        }

        new JobScheduler(regCenter, builder.build(), jobDetail).init();
    }


    private JobDetail createJobDetail(String jobName, Method method, Object instance) {
        JobDetail result = JobBuilder.newJob(LiteJob.class).withIdentity(jobName).build();

        try {
            //addSpringBean(instance);
            ReflectJob reflectionJob = new ReflectJob(method, instance);
            result.getJobDataMap().put(ELASTIC_JOB_DATA_MAP_KEY, reflectionJob);
        } catch (Exception ex) {
            throw new JobConfigurationException("Elastic-Job: Job class '%s' can not initialize.", ReflectJob.class.getCanonicalName());
        }
        return result;
    }

}
