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

    @Resource
    private DataSource dataSource;

    @Bean
    public JobEventConfiguration jobEventConfiguration() {
        return new JobEventRdbConfiguration(dataSource);
    }

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(@Value("${regCenter.serverList}") final String serverList,
                                             @Value("${regCenter.namespace}") final String namespace) {
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverList, namespace));
    }

    @Bean
    public ScheduledAnnotationProcessor scheduledAnnotationProcessor(ZookeeperRegistryCenter zookeeperRegistryCenter,JobEventConfiguration jobEventConfiguration){
        return  new ScheduledAnnotationProcessor(zookeeperRegistryCenter, jobEventConfiguration);
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

