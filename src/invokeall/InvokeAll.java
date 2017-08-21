package invokeall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 */
public class InvokeAll {
    public static void main(String... args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tasks.add(new Task("Task " + i));
        }
        List<Future<Result>> futureList = executorService.invokeAll(tasks);
        executorService.shutdown();
        System.out.println("输入结果");
        for (Future<Result> future : futureList) {
            Result result = future.get();
            System.out.println(result);
        }
    }
}

class Result {

    private String name;
    private int value;



    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + " : " + value;
    }
}

class Task implements Callable<Result> {

    private String name;

    Task(String name) {
        this.name = name;
    }

    @Override
    public Result call() throws Exception {
        System.out.println(name + " 任务开始");
        long time = (long) (Math.random() * 10);
        System.out.println(name + " 计算 " + time + " 等待结果");
        TimeUnit.SECONDS.sleep(time);
        int value = 0;
        for (int i = 0; i < 5; i++) {
            value += Math.random() * 100;
        }
        Result result = new Result();
        result.setName(name);
        result.setValue(value);
        System.out.println(name + " 计算结束 ");
        return result;
    }
}