package com.ami.ddframe.job.util.json;

import com.ami.ddframe.job.api.JobType;
import com.ami.ddframe.job.config.JobCoreConfiguration;
import com.ami.ddframe.job.config.JobRootConfiguration;
import com.ami.ddframe.job.config.JobTypeConfiguration;
import com.ami.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.ami.ddframe.job.config.reflect.ReflectJobCofiguration;
import com.ami.ddframe.job.config.script.ScriptJobConfiguration;
import com.ami.ddframe.job.config.simple.SimpleJobConfiguration;
import com.ami.ddframe.job.executor.handler.JobProperties;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作业配置的Json转换适配器.
 *
 * @param <T> 作业配置对象泛型
 * @author zhangliang
 * @author caohao
 */
public abstract class AbstractJobConfigurationGsonTypeAdapter<T extends JobRootConfiguration> extends TypeAdapter<T> {

    @Override
    public T read(final JsonReader in) throws IOException {
        String jobName = "";
        String cron = "";
        int shardingTotalCount = 0;
        String shardingItemParameters = "";
        String jobParameter = "";
        boolean failover = false;
        boolean misfire = failover;
        String description = "";
        JobProperties jobProperties = new JobProperties();
        JobType jobType = null;
        String jobClass = "";
        boolean streamingProcess = false;
        String scriptCommandLine = "";

        String method = null;
        String instance = null;

        int fixedRate = 0;

        Map<String, Object> customizedValueMap = new HashMap<>(32, 1);
        in.beginObject();
        while (in.hasNext()) {
            String jsonName = in.nextName();
            switch (jsonName) {
                case "jobName":
                    jobName = in.nextString();
                    break;
                case "cron":
                    cron = in.nextString();
                    break;
                case "shardingTotalCount":
                    shardingTotalCount = in.nextInt();
                    break;
                case "shardingItemParameters":
                    shardingItemParameters = in.nextString();
                    break;
                case "jobParameter":
                    jobParameter = in.nextString();
                    break;
                case "failover":
                    failover = in.nextBoolean();
                    break;
                case "misfire":
                    misfire = in.nextBoolean();
                    break;
                case "description":
                    description = in.nextString();
                    break;
                case "jobProperties":
                    jobProperties = getJobProperties(in);
                    break;
                case "jobType":
                    jobType = JobType.valueOf(in.nextString());
                    break;
                case "jobClass":
                    jobClass = in.nextString();
                    break;
                case "streamingProcess":
                    streamingProcess = in.nextBoolean();
                    break;
                case "scriptCommandLine":
                    scriptCommandLine = in.nextString();
                    break;
                case "method":
                    method = in.nextString();
                    break;
                case "instance":
                    instance = in.nextString();
                    break;
                case "fixedRate":
                    fixedRate = in.nextInt();
                    break;
                default:
                    addToCustomizedValueMap(jsonName, in, customizedValueMap);
                    break;
            }
        }
        in.endObject();
        JobCoreConfiguration coreConfig = getJobCoreConfiguration(jobName, cron, fixedRate, shardingTotalCount, shardingItemParameters,
                jobParameter, failover, misfire, description, jobProperties);
        JobTypeConfiguration typeConfig = getJobTypeConfiguration(coreConfig, jobType, jobClass, streamingProcess,
                scriptCommandLine, method, instance);
        return getJobRootConfiguration(typeConfig, customizedValueMap);
    }

    private JobProperties getJobProperties(final JsonReader in) throws IOException {
        JobProperties result = new JobProperties();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "job_exception_handler":
                    result.put(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), in.nextString());
                    break;
                case "executor_service_handler":
                    result.put(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), in.nextString());
                    break;
                default:
                    break;
            }
        }
        in.endObject();
        return result;
    }

    protected abstract void addToCustomizedValueMap(final String jsonName, final JsonReader in, final Map<String, Object> customizedValueMap) throws IOException;

    private JobCoreConfiguration getJobCoreConfiguration(final String jobName, final String cron, final int fixedRate, final int shardingTotalCount,
                                                         final String shardingItemParameters, final String jobParameter, final boolean failover,
                                                         final boolean misfire, final String description,
                                                         final JobProperties jobProperties) {

        if (StringUtils.isEmpty(cron)) {
            return JobCoreConfiguration.newBuilder(jobName, fixedRate, shardingTotalCount)
                    .shardingItemParameters(shardingItemParameters).jobParameter(jobParameter).failover(failover).misfire(misfire).description(description)
                    .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobProperties.get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER))
                    .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), jobProperties.get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER))
                    .build();
        }

        return JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
                .shardingItemParameters(shardingItemParameters).jobParameter(jobParameter).failover(failover).misfire(misfire).description(description)
                .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobProperties.get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER))
                .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), jobProperties.get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER))
                .build();
    }

    private JobTypeConfiguration getJobTypeConfiguration(
            final JobCoreConfiguration coreConfig, final JobType jobType, final String jobClass,
            final boolean streamingProcess, final String scriptCommandLine,
            final String method, final String instance) {
        Preconditions.checkNotNull(jobType, "jobType cannot be null.");
        switch (jobType) {
            case SIMPLE:
                Preconditions.checkArgument(!Strings.isNullOrEmpty(jobClass), "jobClass cannot be empty.");
                return new SimpleJobConfiguration(coreConfig, jobClass);
            case DATAFLOW:
                Preconditions.checkArgument(!Strings.isNullOrEmpty(jobClass), "jobClass cannot be empty.");
                return new DataflowJobConfiguration(coreConfig, jobClass, streamingProcess);
            case SCRIPT:
                return new ScriptJobConfiguration(coreConfig, scriptCommandLine);
            case REFLECT:
                return new ReflectJobCofiguration(coreConfig, jobClass, instance, method);
            default:
                throw new UnsupportedOperationException(String.valueOf(jobType));
        }
    }

    protected abstract T getJobRootConfiguration(final JobTypeConfiguration typeConfig, final Map<String, Object> customizedValueMap);

    @Override
    public void write(final JsonWriter out, final T value) throws IOException {
        out.beginObject();
        out.name("jobName").value(value.getTypeConfig().getCoreConfig().getJobName());
        out.name("jobClass").value(value.getTypeConfig().getJobClass());
        out.name("jobType").value(value.getTypeConfig().getJobType().name());
        out.name("cron").value(value.getTypeConfig().getCoreConfig().getCron());
        out.name("fixedRate").value(value.getTypeConfig().getCoreConfig().getFixedRate());
        out.name("shardingTotalCount").value(value.getTypeConfig().getCoreConfig().getShardingTotalCount());
        out.name("shardingItemParameters").value(value.getTypeConfig().getCoreConfig().getShardingItemParameters());
        out.name("jobParameter").value(value.getTypeConfig().getCoreConfig().getJobParameter());
        out.name("failover").value(value.getTypeConfig().getCoreConfig().isFailover());
        out.name("misfire").value(value.getTypeConfig().getCoreConfig().isMisfire());
        out.name("description").value(value.getTypeConfig().getCoreConfig().getDescription());
        out.name("jobProperties").jsonValue(value.getTypeConfig().getCoreConfig().getJobProperties().json());
        if (value.getTypeConfig().getJobType() == JobType.DATAFLOW) {
            DataflowJobConfiguration dataflowJobConfig = (DataflowJobConfiguration) value.getTypeConfig();
            out.name("streamingProcess").value(dataflowJobConfig.isStreamingProcess());
        } else if (value.getTypeConfig().getJobType() == JobType.SCRIPT) {
            ScriptJobConfiguration scriptJobConfig = (ScriptJobConfiguration) value.getTypeConfig();
            out.name("scriptCommandLine").value(scriptJobConfig.getScriptCommandLine());
        } else if (value.getTypeConfig().getJobType() == JobType.REFLECT) {
            ReflectJobCofiguration reflectJobCofig = (ReflectJobCofiguration) value.getTypeConfig();
            out.name("method").value(reflectJobCofig.getMethod());
            out.name("instance").value(reflectJobCofig.getInstance());
        }

        writeCustomized(out, value);
        out.endObject();
    }

    protected abstract void writeCustomized(final JsonWriter out, final T value) throws IOException;
}
