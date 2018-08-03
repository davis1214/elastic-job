# Elastic-Job - 分布式作业调度解决方案

# 概述

Elastic-Job是一个分布式调度解决方案，由两个相互独立的子项目Elastic-Job-Lite和Elastic-Job-Cloud组成。

Elastic-Job-Lite定位为轻量级无中心化解决方案，使用jar包的形式提供分布式任务的协调服务。

Elastic-Job-Cloud使用Mesos + Docker的解决方案，额外提供资源治理、应用分发以及进程隔离等服务。

# 功能列表

## 1. Elastic-Job-Lite

* 分布式调度协调
* 弹性扩容缩容
* 失效转移
* 错过执行作业重触发
* 作业分片一致性，保证同一分片在分布式环境中仅一个执行实例
* 自诊断并修复分布式不稳定造成的问题
* 支持并行调度
* 支持作业生命周期操作
* 丰富的作业类型
* Spring整合以及命名空间提供
* 运维平台

## 2. Elastic-Job-Cloud
* 应用自动分发
* 基于Fenzo的弹性资源分配
* 分布式调度协调
* 弹性扩容缩容
* 失效转移
* 错过执行作业重触发
* 作业分片一致性，保证同一分片在分布式环境中仅一个执行实例
* 支持并行调度
* 支持作业生命周期操作
* 丰富的作业类型
* Spring整合
* 运维平台
* 基于Docker的进程隔离(TBD)

# 架构图

## Elastic-Job-Lite

![Elastic-Job-Lite Architecture](http://ovfotjrsi.bkt.clouddn.com/docs/img/architecture/elastic_job_lite.png)
***

## Elastic-Job-Cloud

![Elastic-Job-Cloud Architecture](http://ovfotjrsi.bkt.clouddn.com/docs/img/architecture/elastic_job_cloud.png)


# [Release Notes](https://github.com/elasticjob/elastic-job/releases)

# [Roadmap](ROADMAP.md)


