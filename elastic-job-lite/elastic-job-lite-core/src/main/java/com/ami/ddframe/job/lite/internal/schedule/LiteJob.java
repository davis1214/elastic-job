package com.ami.ddframe.job.lite.internal.schedule;

import com.ami.ddframe.job.api.ElasticJob;
import com.ami.ddframe.job.executor.JobExecutorFactory;
import com.ami.ddframe.job.executor.JobFacade;
import lombok.Setter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Lite调度作业.
 *
 * @author zhangliang
 */
public final class LiteJob implements Job {
    
    @Setter
    private ElasticJob elasticJob;
    
    @Setter
    private JobFacade jobFacade;
    
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        JobExecutorFactory.getJobExecutor(elasticJob, jobFacade).execute();
    }
}
