package com.ami.ddframe.job.lite.spring.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: DaviHe
 * @Description:
 * @Date: Created in 2018/6/13
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticScheduler {

    String cron() default "";

    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";

    String jobName() default "";


    int fixedRate() default 0;

}
