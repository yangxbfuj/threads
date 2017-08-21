package chapter7;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

public class CustomRecursiveTask8 {
    public static void main(String... args) {
        int[] array = new int[10000];
        ForkJoinPool pool = new ForkJoinPool();
        Task8 task8 = new Task8("Task ", array, 0, array.length);
        task8.invoke();
        pool.shutdown();
        PrintUtil.println("Main: End.");
    }
}

abstract class MyWorkerTask extends ForkJoinTask<Void> {

    private String name;

    MyWorkerTask(String name) {
        this.name = name;
    }

    @Override
    public Void getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(Void value) {

    }

    @Override
    protected boolean exec() {
        Date startDate = new Date();
        compute();
        Date finishDate = new Date();
        long diff = finishDate.getTime() - startDate.getTime();
        PrintUtil.printf("MyWorkerTask: %s : %d Milliseconds to complete.", name, diff);
        return true;
    }

    String getName() {
        return name;
    }

    abstract void compute();
}

class Task8 extends MyWorkerTask {

    private int array[];
    private int start, end;

    Task8(String name, int[] array, int start, int end) {
        super(name);
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    void compute() {
        if (end - start > 100) {
            int mid = (start + end) / 2;
            Task8 task1 = new Task8(this.getName() + "1", array, start, mid);
            Task8 task2 = new Task8(this.getName() + "2", array, mid, end);
            invokeAll(task1,task2);
        } else {
            for (int i = start; i < end; i++) {
                array[i]++;
            }
        }
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}