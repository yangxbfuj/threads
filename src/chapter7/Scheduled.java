package chapter7;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

/**
 * 定制运行在定时线程池中的任务
 * <p>
 * 定时线程池(Scheduled Thread Pool)是 Executor 框架基本线程池的扩展,允许在一段时间后定时执行任务.
 * ScheduledThreadPoolExecutor 类不仅实现了这个功能,还允许执行以下两类任务:
 * 1.延迟任务,可以执行 Callable 和 Runnable 接口
 * 2.周期性任务,可以执行 Runnable 接口
 */
public class Scheduled {
    public static void main(String... args) throws InterruptedException {
        MyScheduledThreadPoolExecutor executor = new MyScheduledThreadPoolExecutor(2);
        Task task = new Task();
        PrintUtil.println("Main: start at " + new Date());
        executor.schedule(task, 1, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(3);
        task = new Task();
        PrintUtil.println("Main: new task at " + new Date());
        executor.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        PrintUtil.println("Main: End at " + new Date());
    }
}

class MyScheduledTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {

    private RunnableScheduledFuture<V> task;
    private ScheduledThreadPoolExecutor executor;
    private long period;
    private long startDate;

    MyScheduledTask(Runnable runnable, V result, RunnableScheduledFuture<V> task, ScheduledThreadPoolExecutor executor) {
        super(runnable, result);
        this.task = task;
        this.executor = executor;
    }

    @Override
    public boolean isPeriodic() {
        return task.isPeriodic();
    }

    void setPeriod(long period) {
        this.period = period;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        if (!isPeriodic())
            return task.getDelay(unit);
        else {
            Date now = new Date();
            long delay = startDate - now.getTime();
            return unit.convert(delay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public int compareTo(Delayed o) {
        return task.compareTo(o);
    }

    @Override
    public void run() {
        if (isPeriodic() && (!executor.isShutdown())) {
            Date now = new Date();
            startDate = now.getTime() + period;
            executor.getQueue().add(this);
        }
        PrintUtil.println("Pre-MyScheduledTask: " + new Date());
        PrintUtil.println("Pre-MyScheduledTask: is Period " + isPeriodic());
        super.runAndReset();
        PrintUtil.println("Post-MyScheduledTask: " + new Date());
    }
}

class MyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    MyScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * 该方法在增加计划任务时调用
     *
     * @param runnable
     * @param task
     * @param <V>
     * @return
     */
    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        // 经过二次包装
        return new MyScheduledTask<>(runnable, null, task, this);
    }

    /**
     * 该方法在增加周期任务是调用
     *
     * @param command
     * @param initialDelay
     * @param period
     * @param unit
     * @return
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {

        // 在 super.scheduleAtFixedRate 中 调用了 decorateTask 获取到了 ScheduledFuture
        ScheduledFuture<?> task = super.scheduleAtFixedRate(command, initialDelay, period, unit);
        // 所以在此处可以转型
        MyScheduledTask<?> myTask = (MyScheduledTask<?>) task;
        myTask.setPeriod(TimeUnit.MILLISECONDS.convert(period, unit));
        return task;
    }
}

class Task implements Runnable {

    @Override
    public void run() {
        PrintUtil.println("Task: Begin.");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PrintUtil.println("Task: End.");
    }
}

//        output:
//
//        Main: start at Mon Aug 21 11:31:33 CST 2017
//        Pre-MyScheduledTask: Mon Aug 21 11:31:34 CST 2017
//        Pre-MyScheduledTask: is Period false
//        Task: Begin.
//        Main: new task at Mon Aug 21 11:31:36 CST 2017
//        Pre-MyScheduledTask: Mon Aug 21 11:31:36 CST 2017
//        Pre-MyScheduledTask: is Period true
//        Task: Begin.
//        Task: End.
//        Post-MyScheduledTask: Mon Aug 21 11:31:36 CST 2017
//        Task: End.
//        Post-MyScheduledTask: Mon Aug 21 11:31:38 CST 2017
//        Pre-MyScheduledTask: Mon Aug 21 11:31:39 CST 2017
//        Pre-MyScheduledTask: is Period true
//        Task: Begin.
//        Task: End.
//        Post-MyScheduledTask: Mon Aug 21 11:31:41 CST 2017
//        Pre-MyScheduledTask: Mon Aug 21 11:31:42 CST 2017
//        Pre-MyScheduledTask: is Period true
//        Task: Begin.
//        Task: End.
//        Post-MyScheduledTask: Mon Aug 21 11:31:44 CST 2017
//        Pre-MyScheduledTask: Mon Aug 21 11:31:45 CST 2017
//        Pre-MyScheduledTask: is Period true
//        Task: Begin.
//        Task: End.
//        Post-MyScheduledTask: Mon Aug 21 11:31:47 CST 2017
//        Main: End at Mon Aug 21 11:31:47 CST 2017