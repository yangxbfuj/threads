package chapter7;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

public class PriorityExecutor {
    public static void main(String... args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new PriorityBlockingQueue<>());
        for (int i = 0; i < 4; i++) {
            MyPriorityTask task = new MyPriorityTask(i, "Task " + i);
            executor.execute(task);
        }
        for (int i = 4; i < 8; i++) {
            MyPriorityTask task = new MyPriorityTask(i, "Task " + i);
            executor.execute(task);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        PrintUtil.println("Main : End.");
    }
}

class MyPriorityTask implements Runnable, Comparable<MyPriorityTask> {

    private int priority;
    private String name;

    MyPriorityTask(int priority, String name) {
        this.priority = priority;
        this.name = name;
    }

    int getPriority() {
        return priority;
    }

    @Override
    public void run() {
        PrintUtil.printf("任务 %s 优先级为 %d", name, priority);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(MyPriorityTask o) {
        return o.getPriority() - getPriority();
    }
}

//        output
//
//        任务 Task 0 优先级为 0
//        任务 Task 1 优先级为 1
//        任务 Task 7 优先级为 7
//        任务 Task 6 优先级为 6
//        任务 Task 5 优先级为 5
//        任务 Task 4 优先级为 4
//        任务 Task 3 优先级为 3
//        任务 Task 2 优先级为 2
//        Main : End.