package chapter7;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

public class FactoryInExecutor {
    public static void main(String... args) throws InterruptedException {
        MyThreadFactory myThreadFactory = new MyThreadFactory("MyThreadFactory");
        ExecutorService executorService = Executors.newCachedThreadPool(myThreadFactory);
        MyTask task = new MyTask();
        executorService.submit(task);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        PrintUtil.println("Main: End.");
    }
}

