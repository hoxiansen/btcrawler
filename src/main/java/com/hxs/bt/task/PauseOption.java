package com.hxs.bt.task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HJF
 * @date 2018/11/13 11:49
 */
public interface PauseOption {
    void start();

    /**
     * 传入lock 和 condition 暂停指定时间
     *
     * @param lock      锁
     * @param condition 该锁创建的condition
     * @param time      暂停时间
     * @param timeUnit  时间单位
     */
    default void pause(ReentrantLock lock, Condition condition, long time, TimeUnit timeUnit) {
        if (time <= 0)
            return;
        try {
            lock.lock();
            condition.await(time, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 线程等待
     *
     * @param lock      锁
     * @param condition 锁创建的condition
     */
    default void await(ReentrantLock lock, Condition condition) {
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 通知线程不再等待
     *
     * @param lock      锁
     * @param condition condition
     */
    default void signal(ReentrantLock lock, Condition condition) {
        condition.signal();
    }
}
