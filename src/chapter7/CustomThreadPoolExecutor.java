package chapter7;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

/**
 * Executor 框架是一种将线程的创建和执行分离的机制.它基于 Executor 和 ExecutorService 接口,以及两个接口的实现
 * 类 ThreadPoolExecutor 类展开.Executor 内部有一个线程池,并提供了将任务传递到线程池中的方法.可以传递如下两种
 * 任务:
 * 1.Runnable 不返回值
 * 2.Callable<T> 返回类型为 T 的值
 * <p>
 * PS : ThreadPoolExecutor 内部有过一个任务队列
 */
public class CustomThreadPoolExecutor {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        MyExecutor myExecutor = new MyExecutor(2, 4, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SleepTwoSecondsTask task = new SleepTwoSecondsTask();
            Future<String> future = myExecutor.submit(task);
            futures.add(future);
        }
        for (int i = 0; i < 5; i++) {
            String result = futures.get(i).get();
            PrintUtil.printf("Main : Result for Task %d : %s", i, result);
        }
        myExecutor.shutdown();
        for (int i = 5; i < 10; i++) {
            String result = futures.get(i).get();
            PrintUtil.printf("Main : Result for Task %d : %s", i, result);
        }
        myExecutor.awaitTermination(1, TimeUnit.DAYS);
        PrintUtil.println("Main : End.");
    }
}

class MyExecutor extends ThreadPoolExecutor {

    private final String TAG = MyExecutor.class.getSimpleName() + " : ";

    private ConcurrentHashMap<String, Date> startTime;

    public MyExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        startTime = new ConcurrentHashMap<>();
    }

    @Override
    public void shutdown() {
        PrintUtil.println(TAG + "即将 shutdown");
        PrintUtil.println(TAG + "完成任务数量 " + getCompletedTaskCount());
        PrintUtil.println(TAG + "正在运行任务数量 " + getActiveCount());
        PrintUtil.println(TAG + "还未执行的任务数量 " + getQueue().size());
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        PrintUtil.println(TAG + "立即 shutdown");
        PrintUtil.println(TAG + "完成任务数量 " + getCompletedTaskCount());
        PrintUtil.println(TAG + "正在运行任务数量 " + getActiveCount());
        PrintUtil.println(TAG + "还未执行的任务数量 " + getQueue().size());
        return super.shutdownNow();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        PrintUtil.printf(TAG + "一个任务在运行前 进程 : %s 任务 : %s", t.getName(), r.hashCode());
        startTime.put(String.valueOf(r.hashCode()), new Date());
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Future<?> ret = (Future<?>) r;
        try {
            PrintUtil.println("**********************************************");
            PrintUtil.println(TAG + "一个任务已经完成");
            PrintUtil.printf(TAG + "结果为 %s", ret.get());
            Date startDate = startTime.remove(String.valueOf(r.hashCode()));
            long diff = new Date().getTime() - startDate.getTime();
            PrintUtil.printf(TAG + "任务耗时为 %s", diff);
            PrintUtil.println("**********************************************");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        super.afterExecute(r, t);
    }
}

class SleepTwoSecondsTask implements Callable<String> {

    @Override
    public String call() throws Exception {
        TimeUnit.SECONDS.sleep(2);
        return new Date().toString();
    }
}
//        output:
//
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-1 任务 : 825598045
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-2 任务 : 1127673581
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        Main : Result for Task 0 : Mon Aug 21 10:15:49 CST 2017
//        Main : Result for Task 1 : Mon Aug 21 10:15:49 CST 2017
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        MyExecutor : 结果为 Mon Aug 21 10:15:49 CST 2017
//        MyExecutor : 结果为 Mon Aug 21 10:15:49 CST 2017
//        MyExecutor : 任务耗时为 2032
//        **********************************************
//        MyExecutor : 任务耗时为 2032
//        **********************************************
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-2 任务 : 1307642107
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-1 任务 : 1573916557
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        MyExecutor : 结果为 Mon Aug 21 10:15:51 CST 2017
//        Main : Result for Task 2 : Mon Aug 21 10:15:51 CST 2017
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        Main : Result for Task 3 : Mon Aug 21 10:15:51 CST 2017
//        MyExecutor : 任务耗时为 2004
//        **********************************************
//        MyExecutor : 结果为 Mon Aug 21 10:15:51 CST 2017
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-1 任务 : 1323126472
//        MyExecutor : 任务耗时为 2005
//        **********************************************
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-2 任务 : 1238271389
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        Main : Result for Task 4 : Mon Aug 21 10:15:53 CST 2017
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        MyExecutor : 即将 shutdown
//        MyExecutor : 结果为 Mon Aug 21 10:15:53 CST 2017
//        MyExecutor : 结果为 Mon Aug 21 10:15:53 CST 2017
//        MyExecutor : 任务耗时为 2006
//        **********************************************
//        MyExecutor : 任务耗时为 2006
//        **********************************************
//        MyExecutor : 完成任务数量 4
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-1 任务 : 20846907
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-2 任务 : 214696640
//        MyExecutor : 正在运行任务数量 2
//        MyExecutor : 还未执行的任务数量 2    此处发现 4 + 2 + 2 = 8 < 10,猜测应该还有其他的线程状态
//        Main : Result for Task 5 : Mon Aug 21 10:15:53 CST 2017
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        MyExecutor : 结果为 Mon Aug 21 10:15:55 CST 2017
//        Main : Result for Task 6 : Mon Aug 21 10:15:55 CST 2017
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        Main : Result for Task 7 : Mon Aug 21 10:15:55 CST 2017
//        MyExecutor : 任务耗时为 2005
//        **********************************************
//        MyExecutor : 结果为 Mon Aug 21 10:15:55 CST 2017
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-1 任务 : 447629094
//        MyExecutor : 任务耗时为 2005
//        **********************************************
//        MyExecutor : 一个任务在运行前 进程 : pool-1-thread-2 任务 : 2029634610
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        MyExecutor : 结果为 Mon Aug 21 10:15:57 CST 2017
//        MyExecutor : 任务耗时为 2005
//        **********************************************
//        Main : Result for Task 8 : Mon Aug 21 10:15:57 CST 2017
//        **********************************************
//        MyExecutor : 一个任务已经完成
//        MyExecutor : 结果为 Mon Aug 21 10:15:57 CST 2017
//        Main : Result for Task 9 : Mon Aug 21 10:15:57 CST 2017
//        MyExecutor : 任务耗时为 2005
//        **********************************************
//        Main : End.