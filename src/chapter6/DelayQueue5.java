package chapter6;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

/**
 * DelayQueue 具有激活日期属性.集合元素必须实现 Delayed 接口.
 * 1.compareTo(Delayed o)
 * 2.getDelay(TimeUnit unit) 返回剩余时间
 */
public class DelayQueue5 {
    public static void main(String... args) throws InterruptedException {
        DelayQueue<Event5> queue = new DelayQueue<>();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Task5(i + 1, queue));
        }
        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) thread.join();
        int counter = 0;
        do {
            Event5 event;
            do {
                event = queue.poll();
                if (event != null) counter++;
            } while (event != null);
            PrintUtil.printf("At %s you have read %s events", new Date(), counter);
            TimeUnit.MILLISECONDS.sleep(500);
        } while (queue.size() > 0);
    }
}

class Event5 implements Delayed {

    private Date startDate;

    Event5(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        Date now = new Date();
        long diff = startDate.getTime() - now.getTime();
        return diff;
    }

    @Override
    public int compareTo(Delayed o) {
        int ret = 0;
        long result = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (result < 0)
            ret = -1;
        else if (result > 0)
            ret = 1;
        else
            ret = 0;
        return ret;
    }
}

class Task5 implements Runnable {

    private int id;
    private DelayQueue<Event5> queue;

    Task5(int id, DelayQueue<Event5> queue) {
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        Date now = new Date();
        Date delay = new Date();
        delay.setTime(now.getTime() + (id * 1000));
        PrintUtil.printf("Thread %s : %s", id, delay);
        for (int i = 0; i < 100; i++) {
            Event5 event = new Event5(delay);
            queue.add(event);
        }
    }
}