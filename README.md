# Elastic-Job - distributed scheduled job solution

# [Homepage](http://elasticjob.io/)

# [中文主页](http://elasticjob.io/index_zh.html)

# Elastic-Job-Lite Console [![GitHub release](https://img.shields.io/badge/release-download-orange.svg)](https://elasticjob.io/dist/elastic-job-lite-console-2.1.5.tar.gz)

# Elastic-Job-Cloud Framework[![GitHub release](https://img.shields.io/badge/release-download-orange.svg)](https://elasticjob.io/dist/elastic-job-cloud-scheduler-2.1.5.tar.gz)

# Overview

Elastic-Job is a distributed scheduled job solution. Elastic-Job is composited from 2 independent sub projects: Elastic-Job-Lite and Elastic-Job-Cloud.

Elastic-Job-Lite is a centre-less solution, use lightweight jar to coordinate distributed jobs.
Elastic-Job-Cloud is a Mesos framework which use Mesos + Docker(todo) to manage and isolate resources and processes.

Elastic-Job-Lite and Elastic-Job-Cloud provide unified API. Developers only need code one time, then decide to deploy Lite or Cloud as you want.

# Features

## 1. Elastic-Job-Lite

* Distributed schedule job coordinate
* Elastic scale in and scale out supported
* Failover
* Misfired jobs refire
* Sharding consistently, same sharding item for a job only one running instance
* Self diagnose and recover when distribute environment unstable
* Parallel scheduling supported
* Job lifecycle operation
* Lavish job types
* Spring integrated and namespace supported
* Web console

## 2. Elastic-Job-Cloud
* All Elastic-Job-Lite features included
* Application distributed automatically
* Fenzo based resources allocated elastically
* Docker based processes isolation support (TBD)

# Architecture

## Elastic-Job-Lite

![Elastic-Job-Lite Architecture](http://ovfotjrsi.bkt.clouddn.com/docs/img/architecture/elastic_job_lite.png)
***

## Elastic-Job-Cloud

![Elastic-Job-Cloud Architecture](http://ovfotjrsi.bkt.clouddn.com/docs/img/architecture/elastic_job_cloud.png)


# [Release Notes](https://github.com/elasticjob/elastic-job/releases)

# [Roadmap](ROADMAP.md)

# Quick Start

## Elastic-Job-Lite

### re dev (in ami)
1. 增加新的trigger类型（simpleTrigger，以实现fixRate调度功能）
2. 增加注解 Scheduler(crontab)，简化开发难度
3. 增加分片策略配置入口
4. 增加spring bean初始化后的自动扫描（ScheduledAnnotationProcessor）
5. console平台，增加任务运行时所在的服务器和进程ID
6. console平台，作业历史列表根据时间倒叙
7. console服务作业操作页面，增加fixRate的展示
8. console去掉关于当当相关的配置
9. console，任务修改页面增加“fixedRate”属性
10.增加zk是否重新加载的属性配置
11.增加作业调度使用类型（fixed / delay …… ）


### Add maven dependency

```xml
<!-- import elastic-job lite core -->
<dependency>
    <groupId>com.ami</groupId>
    <artifactId>elastic-job-lite-core</artifactId>
    <version>${lasted.release.version}</version>
</dependency>

<!-- import other module if need -->
<dependency>
    <groupId>com.ami</groupId>
    <artifactId>elastic-job-lite-spring</artifactId>
    <version>${lasted.release.version}</version>
</dependency>
```
### Job config

```java
@Slf4j
@Component
@ConditionalOnExpression("${scheduler.enable:false}")
public class SchedulerConfig {

    @Value("${scheduler.dbstorage.enable:false}")
    private boolean enableDbStorage;

    @Value("${scheduler.regCenter.overwrite:true}")
    private boolean shouldOverwriteliteJobConfig;

    @Autowired
    private ApplicationContext applicationContext;


    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(@Value("${scheduler.regCenter.serverList}") final String serverList,
                                             @Value("${scheduler.regCenter.namespace}") final String namespace) {
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverList, namespace));
    }

    @Bean
    public ScheduledAnnotationProcessor scheduledAnnotationProcessor(ZookeeperRegistryCenter zookeeperRegistryCenter) {
        String shardingStrategyClass = "com.select.ex.ddframe.job.lite.api.strategy.impl.RotateServerByNameJobShardingStrategy";

        if (enableDbStorage) {
            DataSource dataSource = (DataSource) applicationContext.getBean("getOrzDataSources");
            log.info("init elastic job with db storage");
            return new ScheduledAnnotationProcessor(zookeeperRegistryCenter, new JobEventRdbConfiguration(dataSource), shardingStrategyClass, shouldOverwriteliteJobConfig);
        }
        return new ScheduledAnnotationProcessor(zookeeperRegistryCenter, shardingStrategyClass, shouldOverwriteliteJobConfig);
    }
}


    
```

### Job development
```java

    @Autowired
    private HelloContorller helloContorller;

    @Scheduler(cron = "0/10 * * * * ?" ,jobName = "job-name-a")
    public void schedulerA() {
        String uid = "1a7390fffb7546879a42cf7f16d378f9";
        final String ret = helloContorller.addData4(uid);

        log.info("schedulerA -> @Scheduler date {} , ret {}", new Date(), ret);
    }

    @Scheduler(fixedRate = 20 ,jobName = "job-name-b")
    public void schedulerB() {
        log.info("schedulerB -> @Scheduler date {} , fixedRate {}", new Date(), 20L);
    }
    
```

***

## Elastic-Job-Cloud

### Add maven dependency

```xml
<!-- import elastic-job cloud executor -->
<dependency>
    <groupId>com.ami</groupId>
    <artifactId>elastic-job-cloud-executor</artifactId>
    <version>${lasted.release.version}</version>
</dependency>
```

### Job development

Same with `Elastic-Job-Lite`

### Job App configuration

```shell
curl -l -H "Content-type: application/json" -X POST -d '{"appName":"yourAppName","appURL":"http://app_host:8080/foo-job.tar.gz","cpuCount":0.1,"memoryMB":64.0,"bootstrapScript":"bin/start.sh","appCacheEnable":true}' http://elastic_job_cloud_host:8899/api/app
```

### Job configuration

```shell
curl -l -H "Content-type: application/json" -X POST -d '{"jobName":"foo_job","appName":"yourAppName","jobClass":"yourJobClass","jobType":"SIMPLE","jobExecutionType":"TRANSIENT","cron":"0/5 * * * * ?","shardingTotalCount":5,"cpuCount":0.1,"memoryMB":64.0,"failover":true,"misfire":true,"bootstrapScript":"bin/start.sh"}' http://elastic_job_cloud_host:8899/api/job/register
```

