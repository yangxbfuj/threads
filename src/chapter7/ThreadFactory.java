package chapter7;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

public class ThreadFactory {
    public static void main(String... args) throws InterruptedException {
        MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
        MyTask task = new MyTask();
        Thread thread = factory.newThread(task);
        thread.start();
        thread.join();
        PrintUtil.println("Main: Thread information.");
        PrintUtil.println(thread.toString());
        PrintUtil.println("Main: End.");
    }
}

class MyThread extends Thread {

    private Date creationDate;
    private Date startDate;
    private Date finishDate;

    MyThread(Runnable target, String name) {
        super(target, name);
        setCreationDate();
    }

    @Override
    public void run() {
        setStartDate();
        super.run();
        setFinishDate();
    }

    void setCreationDate() {
        this.creationDate = new Date();
    }

    void setStartDate() {
        this.startDate = new Date();
    }

    void setFinishDate() {
        this.finishDate = new Date();
    }

    long getExecuteTime() {
        return finishDate.getTime() - startDate.getTime();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getName());
        stringBuilder.append(": ");
        stringBuilder.append(" creation Date: ");
        stringBuilder.append(creationDate);
        stringBuilder.append(" Running Time:");
        stringBuilder.append(getExecuteTime());
        stringBuilder.append(" Milliseconds.");
        return stringBuilder.toString();
    }
}

class MyThreadFactory implements java.util.concurrent.ThreadFactory {
    private int counter;
    private String prefix;

    MyThreadFactory(String prefix) {
        this.prefix = prefix;
        counter = 1;
    }

    @Override
    public Thread newThread(Runnable r) {
        MyThread myThread = new MyThread(r, prefix + "-" + counter);
        counter++;
        return myThread;
    }
}

class MyTask implements Runnable {

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//        output:
//
//        Main: Thread information.
//        MyThreadFactory-1:  creation Date: Mon Aug 21 11:04:22 CST 2017 Running Time:2005 Milliseconds.
//        Main: End.