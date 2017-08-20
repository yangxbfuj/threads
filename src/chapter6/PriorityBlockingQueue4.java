package chapter6;

import java.util.concurrent.PriorityBlockingQueue;

import utils.PrintUtil;

/**
 * PriorityBlockingQueue 的元素必须实现 Comparable 接口.
 */
public class PriorityBlockingQueue4 {
    public static void main(String... args) throws InterruptedException {
        PriorityBlockingQueue<Event> events = new PriorityBlockingQueue<>();
        Thread taskThreads[] = new Thread[5];
        for (int i = 0; i < taskThreads.length; i++) {
            Task task = new Task(i, events);
            taskThreads[i] = new Thread(task);
        }
        for (int i = 0; i < taskThreads.length; i++) {
            taskThreads[i].start();
        }
        // 等待创建列表的线程执行完毕
        for (int i = 0; i < taskThreads.length; i++) {
            taskThreads[i].join();
        }
        PrintUtil.println("Main : Queue Size : " + events.size());
        for (int i = 0; i < taskThreads.length * 1000; i++) {
            Event event = events.poll();
            PrintUtil.printf("Thread %s : Priority %d ", event.getThread(), event.getPriority());
        }
        PrintUtil.println("Main : Queue Size : " + events.size());
        PrintUtil.println("Main : End of the program.");
    }
}

class Event implements Comparable<Event> {

    private int thread;
    private int priority;

    Event(int thread, int priority) {
        this.thread = thread;
        this.priority = priority;
    }

    public int getThread() {
        return thread;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Event o) {
        return o.getPriority() - priority;
    }
}

class Task implements Runnable {

    private int id;
    private PriorityBlockingQueue<Event> queue;

    Task(int id, PriorityBlockingQueue<Event> queue) {
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            Event event = new Event(id, i);
            queue.add(event);
        }
    }
}