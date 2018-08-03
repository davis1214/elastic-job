package com.ami.ddframe.job.config.reflect;

import com.ami.ddframe.job.api.JobType;
import com.ami.ddframe.job.config.JobCoreConfiguration;
import com.ami.ddframe.job.config.JobTypeConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * @param
 * @Author: DaviHe
 */
@RequiredArgsConstructor
@Getter
public final class ReflectJobCofiguration implements JobTypeConfiguration {

    private final JobCoreConfiguration coreConfig;

    private final JobType jobType = JobType.REFLECT;

    private final String jobClass;

    private final String instance;

    private final String method;
}
