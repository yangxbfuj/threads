package done;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class DoneDemo {
    public static void main(String... args) throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newCachedThreadPool();
        ResultTask resultTasks[] = new ResultTask[5];
        for (int i = 0; i < 5; i++) {
            resultTasks[i] = new ResultTask(new ExecutableTask("Task " + i));
            service.submit(resultTasks[i]);
        }
        TimeUnit.SECONDS.sleep(5);
        for (ResultTask resultTask : resultTasks) {
            resultTask.cancel(true);
        }
        for (ResultTask resultTask : resultTasks) {
            if (!resultTask.isCancelled()) {
                System.out.println(resultTask.get());
            }
        }
        service.shutdown();
    }
}

class ExecutableTask implements Callable<String> {

    private String name;

    String getName() {
        return name;
    }

    ExecutableTask(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        long time = (long) (Math.random() * 10);
        System.out.printf("%s 执行 %d\n", name, time);
        TimeUnit.SECONDS.sleep(time);
        return "Hello world.I'm " + name;
    }
}

class ResultTask extends FutureTask<String> {

    private String name;

    ResultTask(ExecutableTask callable) {
        super(callable);
        this.name = callable.getName();
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            System.out.printf("%s : %s has been canceled\n", Thread.currentThread().getName(), name);
        } else {
            System.out.printf("%s : %s has been finish\n", Thread.currentThread().getName(), name);
        }
    }
}