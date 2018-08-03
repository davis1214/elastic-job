package com.ami.ddframe.job.lite.spring.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: DaviHe
 * @Description:
 * @Date: Created in 2018/6/14
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ElasticSchedulers {

    ElasticScheduler[] value();

}