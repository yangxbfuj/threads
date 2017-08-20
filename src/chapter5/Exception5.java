package chapter5;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

public class Exception5 {
    public static void main(String... args) throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        int array[] = new int[100];
        Task5 task5 = new Task5(array, 0, 100);
        forkJoinPool.execute(task5);
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.DAYS);
        if (task5.isCompletedAbnormally()) {
            PrintUtil.println("Main : An exception has occurred.");
            PrintUtil.println("Main : Exception msg is " + task5.getException().getMessage());
        }
        PrintUtil.printf("Main : Result : %d ", task5.join());
    }
}

class Task5 extends RecursiveTask<Integer> {

    private int array[];
    private int start, end;

    Task5(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        PrintUtil.printf("Task : Start from %d to %d ", start, end);
        if (end - start < 10) {
            if (3 > start && 3 < end) {
                RuntimeException e = new RuntimeException("This task throw an Exception : Task from " + start + " to " + end);
                completeExceptionally(e);
                //throw e;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            int mid = (start + end) / 2;
            Task5 task1 = new Task5(array, start, mid);
            Task5 task2 = new Task5(array, mid, end);
            invokeAll(task1, task2);
        }
        PrintUtil.printf("Task : End from %d to %d ", start, end);
        return 0;
    }
}
//        output:
//        Task : Start from 0 to 100
//        Task : Start from 0 to 50
//        Task : Start from 50 to 100
//        Task : Start from 0 to 25
//        Task : Start from 50 to 75
//        Task : Start from 50 to 62
//        Task : Start from 50 to 56
//        Task : Start from 25 to 50
//        Task : Start from 25 to 37
//        Task : Start from 25 to 31
//        Task : Start from 75 to 100
//        Task : Start from 0 to 12
//        Task : Start from 75 to 87
//        Task : Start from 0 to 6
//        Task : Start from 75 to 81
//        Task : End from 50 to 56
//        Task : Start from 56 to 62
//        Task : End from 25 to 31
//        Task : Start from 31 to 37
//        Task : End from 75 to 81
//        Task : Start from 81 to 87
//        Task : End from 0 to 6
//        Task : Start from 6 to 12
//        Task : End from 56 to 62
//        Task : End from 50 to 62
//        Task : Start from 62 to 75
//        Task : Start from 62 to 68
//        Task : End from 31 to 37
//        Task : End from 25 to 37
//        Task : Start from 37 to 50
//        Task : Start from 37 to 43
//        Task : End from 81 to 87
//        Task : End from 75 to 87
//        Task : Start from 87 to 100
//        Task : Start from 87 to 93
//        Task : End from 6 to 12
//        Task : Start from 12 to 25
//        Task : Start from 12 to 18
//        Task : End from 37 to 43
//        Task : Start from 43 to 50
//        Task : End from 62 to 68
//        Task : Start from 68 to 75
//        Task : End from 87 to 93
//        Task : Start from 93 to 100
//        Task : End from 12 to 18
//        Task : Start from 18 to 25
//        Exception in thread "main" Task : End from 43 to 50
//        Task : End from 37 to 50
//        Task : End from 25 to 50
//        Task : End from 68 to 75
//        Task : End from 62 to 75
//        Task : End from 50 to 75
//        Task : End from 18 to 25
//        Task : End from 12 to 25
//        Task : End from 93 to 100
//        Task : End from 87 to 100
//        Task : End from 75 to 100
//        Task : End from 50 to 100
//        Main : An exception has occurred.
//        Main : Exception msg is java.lang.RuntimeException: This task throw an Exception : Task from 0 to 6
//        java.lang.RuntimeException: java.lang.RuntimeException: This task throw an Exception : Task from 0 to 6
//        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
//        at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
//        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
//        at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
//        at java.util.concurrent.ForkJoinTask.getThrowableException(ForkJoinTask.java:593)
//        at java.util.concurrent.ForkJoinTask.reportException(ForkJoinTask.java:677)
//        at java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:720)
//        at chapter5.Exception5.main(Exception5.java:21)
//        Caused by: java.lang.RuntimeException: This task throw an Exception : Task from 0 to 6
//        at chapter5.Task5.compute(Exception5.java:41)
//        at chapter5.Task5.compute(Exception5.java:25)
//        at java.util.concurrent.RecursiveTask.exec(RecursiveTask.java:94)
//        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
//        at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:401)
//        at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:759)
//        at chapter5.Task5.compute(Exception5.java:54)
//        at chapter5.Task5.compute(Exception5.java:25)
//        at java.util.concurrent.RecursiveTask.exec(RecursiveTask.java:94)
//        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
//        at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:401)
//        at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:759)
//        at chapter5.Task5.compute(Exception5.java:54)
//        at chapter5.Task5.compute(Exception5.java:25)
//        at java.util.concurrent.RecursiveTask.exec(RecursiveTask.java:94)
//        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
//        at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:401)
//        at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:759)
//        at chapter5.Task5.compute(Exception5.java:54)
//        at chapter5.Task5.compute(Exception5.java:25)
//        at java.util.concurrent.RecursiveTask.exec(RecursiveTask.java:94)
//        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
//        at java.util.concurrent.ForkJoinTask.doInvoke(ForkJoinTask.java:401)
//        at java.util.concurrent.ForkJoinTask.invokeAll(ForkJoinTask.java:759)
//        at chapter5.Task5.compute(Exception5.java:54)
//        at chapter5.Task5.compute(Exception5.java:25)
//        at java.util.concurrent.RecursiveTask.exec(RecursiveTask.java:94)
//        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
//        at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
//        at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
//        at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)