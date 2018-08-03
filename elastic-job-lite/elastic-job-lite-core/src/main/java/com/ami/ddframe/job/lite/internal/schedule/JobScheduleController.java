package com.ami.ddframe.job.lite.internal.schedule;

import com.ami.ddframe.job.exception.JobSystemException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;

/**
 * 作业调度控制器.
 *
 * @author zhangliang
 */
@RequiredArgsConstructor
public final class JobScheduleController {

    private final Scheduler scheduler;

    private final JobDetail jobDetail;

    private final String triggerIdentity;

    /**
     * 调度作业.
     *
     * @param cron CRON表达式
     */
    public void scheduleJob(final String cron, final int fixedRate) {
        try {
            if (!scheduler.checkExists(jobDetail.getKey())) {

                if (StringUtils.isNotEmpty(cron)) {
                    scheduler.scheduleJob(jobDetail, createTrigger(cron));
                } else {
                    scheduler.scheduleJob(jobDetail, createTrigger(fixedRate));
                }
            }
            scheduler.start();
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    public void scheduleJob(final String cron) {
        try {
            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, createTrigger(cron));
            }
            scheduler.start();
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    public void scheduleJob(final int fixedRate) {
        try {
            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, createTrigger(fixedRate));
            }
            scheduler.start();
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    /**
     * 重新调度作业.
     *
     * @param cron CRON表达式
     */
    public synchronized void rescheduleJob(final String cron, final int fixedRate) {
        try {

            if (!StringUtils.isEmpty(cron)) {
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
                if (!scheduler.isShutdown() && null != trigger && !cron.equals(trigger.getCronExpression())) {
                    scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(cron));
                }
                return;
            } else if (fixedRate > 0) {
                SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
                if (!scheduler.isShutdown() && null != trigger) {
                    long fixedIntervals = fixedRate;
                    if (fixedRate > 0 && fixedIntervals != trigger.getRepeatInterval()) {
                        scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(fixedRate));
                    }
                }
            }

        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    public synchronized void rescheduleJob(final int fixedRate) {
        try {
            SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));

            long fixedIntervals = fixedRate;
            if (!scheduler.isShutdown() && null != trigger && fixedIntervals != trigger.getRepeatInterval()) {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(fixedRate));
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }


    public synchronized void rescheduleJob(final String cron) {
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
            if (!scheduler.isShutdown() && null != trigger && !cron.equals(trigger.getCronExpression())) {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(cron));
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    private CronTrigger createTrigger(final String cron) {
        return TriggerBuilder.newTrigger().withIdentity(triggerIdentity)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
                .build();
    }

    private SimpleTrigger createTrigger(final int fixedRate) {
        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger().withIdentity(triggerIdentity)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(fixedRate)
                        .repeatForever()
                )
                .build();
        return trigger;
    }

    /**
     * 判断作业是否暂停.
     *
     * @return 作业是否暂停
     */
    public synchronized boolean isPaused() {
        try {
            return !scheduler.isShutdown() && Trigger.TriggerState.PAUSED == scheduler.getTriggerState(new TriggerKey(triggerIdentity));
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    /**
     * 暂停作业.
     */
    public synchronized void pauseJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.pauseAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    /**
     * 恢复作业.
     */
    public synchronized void resumeJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.resumeAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    /**
     * 立刻启动作业.
     */
    public synchronized void triggerJob() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.triggerJob(jobDetail.getKey());
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    /**
     * 关闭调度器.
     */
    public synchronized void shutdown() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
}
