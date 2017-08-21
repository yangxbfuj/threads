package delay;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduleDemo {
    public static void main(String... args) throws InterruptedException {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        System.out.printf("Main : Start at %s\n", new Date());
        for (int i = 0; i < 5; i++) {
            Task task = new Task("Task " + i);
            executor.scheduleAtFixedRate(() -> {
                try {
                    task.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, i + 1, 1, TimeUnit.SECONDS);
        }
//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.printf("Main : End at %s \n", new Date());
    }
}

class Task implements Callable<String> {

    private String name;

    Task(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        System.out.printf("%s : Starting at : %s\n", name, new Date());
        return "Hello World!";
    }
}