package com.ami.ddframe.job.api.reflect;

import com.ami.ddframe.job.api.ShardingContext;
import com.ami.ddframe.job.api.simple.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @param
 * @Author: DaviHe
 * @Description:
 * @Date: Created in 2018/6/14
 */
public class ReflectJob implements SimpleJob {
    private final Logger log = LoggerFactory.getLogger(ReflectJob.class);

    private String methodName;
    private String instanceName;

    private Method method;
    private Object instance;

    public ReflectJob() {
    }

    public ReflectJob(Method method, Object instance) {
        this.instance = instance;
        this.method = method;
        this.instanceName = this.instance.getClass().getCanonicalName();
        this.methodName = this.method.getName();
    }

    public ReflectJob(String methodName, String instanceName) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException {
        this.methodName = methodName;
        this.instanceName = instanceName;
        this.instance = Class.forName(instanceName).newInstance();
        this.method = this.instance.getClass().getMethod(this.methodName);
    }

    @Override
    public void execute(final ShardingContext shardingContext) {

        log.info(String.format("Item: %s | Time: %s | Thread: %s | %s",
                shardingContext.getShardingItem(), new SimpleDateFormat("HH:mm:ss").format(new Date()),
                Thread.currentThread().getId(), "REFLECT"));
        long start = System.currentTimeMillis();

        try {
            method.invoke(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        log.info("executed method [{} -> {}] ,sharding info [total {} , item {}] ,costs {} ms", instanceName, methodName,
                shardingContext.getShardingTotalCount(), shardingContext.getShardingItem(), (System.currentTimeMillis() - start));
    }

}
