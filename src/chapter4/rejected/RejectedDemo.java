package chapter4.rejected;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

/**
 * 处理在执行器中被拒绝的任务
 */
public class RejectedDemo {
    public static void main(String... args) {
        RejectedExecutionHandler rejectedExecutionHandler = new RejectedTaskController();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);

        PrintUtil.println("模拟开始");
        for (int j = 0; j < 3; j++) {
            executor.submit(new Task("Task " + j));
        }
        executor.shutdown();
        PrintUtil.println("关闭线程池执行器");
        PrintUtil.println("加入一个新的任务");
        executor.submit(new Task("Task new"));
        PrintUtil.println("主线程执行完毕");
    }
}

class RejectedTaskController implements RejectedExecutionHandler {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        PrintUtil.printf(TAG + ": 任务 %s 被拒绝", r.toString());
        PrintUtil.println(TAG + ": 拒绝处理方法所在线程为 " + Thread.currentThread().getName());
        PrintUtil.printf(TAG + ": %s", executor.toString());
        PrintUtil.printf(TAG + ": 结束中 : %s", executor.isTerminating());
        PrintUtil.printf(TAG + ": 已结束 : %s", executor.isTerminated());
    }
}

class Task implements Runnable {

    private String name;

    Task(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        PrintUtil.printf("任务 %s 开始", name);
        long duration = (long) (Math.random() * 10);
        PrintUtil.printf("任务 %s : 创建一个任务,耗时为 %d", name, duration);
        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PrintUtil.printf("任务 %s : 结束", name);
    }

    @Override
    public String toString() {
        return name;
    }
}

/**
 * output
 */
//        模拟开始
//        任务 Task 0 开始
//        任务 Task 2 开始
//        关闭线程池执行器
//        加入一个新的任务
//        任务 Task 1 开始
//        RejectedTaskController: 任务 java.util.concurrent.FutureTask@5e481248 被拒绝
//        RejectedTaskController: 拒绝处理方法所在线程为 main
//        RejectedTaskController: java.util.concurrent.ThreadPoolExecutor@66d3c617[Shutting down, pool size = 3, active threads = 3, queued tasks = 0, completed tasks = 0]
//        RejectedTaskController: 结束中 : true
//        RejectedTaskController: 已结束 : false
//        主线程执行完毕
//        任务 Task 1 : 创建一个任务,耗时为 2
//        任务 Task 0 : 创建一个任务,耗时为 1
//        任务 Task 2 : 创建一个任务,耗时为 4
//        任务 Task 0 : 结束
//        任务 Task 1 : 结束
//        任务 Task 2 : 结束