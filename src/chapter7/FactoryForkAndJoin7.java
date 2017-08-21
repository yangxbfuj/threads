package chapter7;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

/**
 * Fork/Join 是 Executor 和 ExecutorService 接口的实现,这两个接口允许我们执行 Callable 和 Runnable 任务,而
 * 不需要去关注执行这些任务的具体线程.
 * 这个执行器用于执行可以拆分成更小任务体的任务,它的主要组件如下.
 * 1.一种特殊类型任务,用 ForkJoinTask 类来实现.
 * 2.两种操作,其中通过 fork 操作将一个任务分拆为过个子任务,而通过 join 操作等待这些子任务结束.
 * 3.工作窃取算法(Work-Stealing Algorithm),用来对线程池的使用进行优化.当一个任务等待他的子任务时,执行这个任务的
 * 线程可以被用来执行其他任务.
 * <p>
 * Fork/Join 框架的主类是 ForkJoinPool 类.从内部实现来说,它有下面两个元素:
 * 1.一个任务队列,存放的是等待被执行的任务;
 * 2.一个执行这些任务的线程池.
 * <p>
 * 本实例,将学习如何实现一个定制的工作线程(Worker Thread),它被 ForkJoinPool 类使用,此外我们还将学习如何通过工厂
 * 模式来使用它.
 */
public class FactoryForkAndJoin7 {
    public static void main(String... args) throws InterruptedException, ExecutionException {
        MyWorkerThreadFactory factory = new MyWorkerThreadFactory();
        ForkJoinPool pool = new ForkJoinPool(4, factory, null, false);
        int[] array = new int[1000000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        MyRecursiveTask task = new MyRecursiveTask(array, 0, array.length);
        pool.execute(task);
        task.join();
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
        PrintUtil.printf("Main : Result : %d", task.get());
        PrintUtil.printf("Main : End");
    }
}

class MyWorkerThread extends ForkJoinWorkerThread {

    private static ThreadLocal<Integer> taskCounter = new ThreadLocal<>();

    /**
     * Creates a ForkJoinWorkerThread operating in the given pool.
     *
     * @param pool the pool this thread works in
     * @throws NullPointerException if pool is null
     */
    MyWorkerThread(ForkJoinPool pool) {
        super(pool);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PrintUtil.printf("MyWorkerThread %d : Initializing task counter", getId());
        taskCounter.set(0);
    }

    @Override
    protected void onTermination(Throwable exception) {
        PrintUtil.printf("MyWorkerThread %d: %d", getId(), taskCounter.get());
        super.onTermination(exception);
    }

    void addTask() {
        int counter = taskCounter.get().intValue();
        counter++;
        taskCounter.set(counter);
    }
}

class MyWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new MyWorkerThread(pool);
    }
}

class MyRecursiveTask extends RecursiveTask<Integer> {

    private int[] array;
    private int start, end;

    MyRecursiveTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int ret;
        MyWorkerThread thread = (MyWorkerThread) Thread.currentThread();
        thread.addTask();
        if (end - start < 100) {
            return 1;
        } else {
            int mid = (start + end) / 2;
            MyRecursiveTask task1 = new MyRecursiveTask(array, start, mid);
            MyRecursiveTask task2 = new MyRecursiveTask(array, mid, end);
            task1.fork();
            task2.fork();
            return addResults(task1, task2) + 1;
        }
    }

    Integer addResults(MyRecursiveTask task1, MyRecursiveTask task2) {
        int value;
        try {
            value = task1.get().intValue() + task2.get().intValue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            value = 0;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }
}

// 在 Fork/Join 框架里的线程被称作工作线程(Worker Thread).Java 提供了 ForkJoinWorkerThread 类,它继承了
// Thread 类并实现了可以在 Fork/Join 框架里使用的工作线程.
