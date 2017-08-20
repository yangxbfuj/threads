package chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

/**
 * 在任务开始之前可以取消,但要注意一下两点:
 * 1.ForkJoinPool 类不提供任何方法来取消线程池中正在运行或者等待运行的所有线程
 * 2.取消任务时,不能取消已经被执行的任务
 * <p>
 * 范例中将寻找某个数组中某个数字所处的位置.第一个任务是寻找可以被取消的剩余任务数.由于 Fork/Join 框架没有提供取消
 * 功能,我们将创建一个辅助类实现取消任务的操作.
 */
public class CancelTask6 {

    private static final int ARRAY_NUM = 1000;

    public static void main(String... args) throws InterruptedException, ExecutionException {
        int[] array = new ArrayGenerator().generateArray(ARRAY_NUM);
        TaskManager taskManager = new TaskManager();
        ForkJoinPool pool = new ForkJoinPool();
        SearchNumberTask task = new SearchNumberTask(array, 0, ARRAY_NUM, 5, taskManager);
        pool.execute(task);
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
        PrintUtil.printf("Main : The program has finished");
        PrintUtil.printf("Main : Found at " + task.getWhere());
    }
}

class ArrayGenerator {
    int[] generateArray(int size) {
        int array[] = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(10);
        }
        return array;
    }
}

class TaskManager {
    private List<ForkJoinTask<Integer>> tasks;

    TaskManager() {
        tasks = new ArrayList<>();
    }

    void addTask(ForkJoinTask<Integer> task) {
        tasks.add(task);
    }

    void cancelTasks(ForkJoinTask<Integer> cancelTask) {
        for (ForkJoinTask task : tasks) {
            // 不是当前任务,设置取消
            if (task != cancelTask) {
                task.cancel(true);
                ((SearchNumberTask) task).writeCancelMessage();
            }
        }
    }
}

class SearchNumberTask extends RecursiveTask<Integer> {

    private int[] numbers;
    private int start, end;
    private int number;
    private TaskManager taskManager;
    private final static int NOT_FOUND = -1;
    private static Integer where;

    SearchNumberTask(int[] numbers, int start, int end, int number, TaskManager taskManager) {
        this.end = end;
        this.number = number;
        this.numbers = numbers;
        this.start = start;
        this.taskManager = taskManager;
    }

    public Integer getWhere() {
        return where;
    }

    @Override
    protected Integer compute() {
        PrintUtil.println("Task : " + start + " : " + end);
        int ret;
        if (end - start > 10) {
            ret = launchTasks();
        } else {
            ret = lookForNumber();
        }
        return ret;
    }

    private int launchTasks() {
        int mid = (start + end) / 2;
        SearchNumberTask task1 = new SearchNumberTask(numbers, start, mid, number, taskManager);
        SearchNumberTask task2 = new SearchNumberTask(numbers, mid, end, number, taskManager);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        task1.fork();
        task2.fork();
        int ret;
        ret = task1.join();
        if (ret != -1) {
            return ret;
        }
        return task2.join();
    }

    private int lookForNumber() {
        for (int i = start; i < end; i++) {
            if (numbers[i] == number) {
                System.err.printf("Task : Number %d found in position %d \n", number, i);
                where = i;
                taskManager.cancelTasks(this);
                return i;
            }
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return NOT_FOUND;
    }

    void writeCancelMessage() {
        PrintUtil.printf("Task : Canceled task form %d to %d", start, end);
    }
}
