package excuter;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */
public class WebDemo {
    public static void main(String... args) {
        long startTime = System.currentTimeMillis();
        Server server = new Server();
        int i = 100;
        while (i-- > 0) {
            Task task = new Task("线程 " + i);
            server.executeTask(task);
        }
        server.endServer();
        long endTime = System.currentTimeMillis();
        System.err.println("全部耗时: " + (endTime - startTime));
    }
}

class Task implements Runnable {

    private Date initDate;
    private String name;

    Task(String name) {
        initDate = new Date();
        this.name = name;
    }

    String getName() {
        return name;
    }

    @Override
    public void run() {
        System.out.printf("%s : %s 创建 %s \n", Thread.currentThread().getName(), name, initDate);
        System.out.printf("%s : %s 开始 %s \n", Thread.currentThread().getName(), name, new Date());
        double duration = Math.random() * 10;
        System.out.printf("%s : %s 耗时 %d \n", Thread.currentThread().getName(), name, (long) 2);
        try {
            TimeUnit.MILLISECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s : %s 完成 %s \n", Thread.currentThread().getName(), name, new Date());
    }
}

class Server {

    private ThreadPoolExecutor executor;

    Server() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    void executeTask(Task task) {
        System.out.println("服务器:收到新任务 " + task.getName());
        executor.execute(task);
        System.out.println("服务器:线程池大小为 " + executor.getPoolSize());
        System.out.println("服务器:活跃线程数为 " + executor.getActiveCount());
        System.out.println("服务器:完成线程数为 " + executor.getCompletedTaskCount());
        System.out.println("服务器:线程数" + executor.getTaskCount());
    }

    void endServer() {
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
