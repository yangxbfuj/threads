package chapter6;

import java.util.concurrent.ConcurrentLinkedDeque;

import utils.PrintUtil;

/**
 * 非阻塞式列表提供了一些操作,如果被执行的操作不能立即执行(例如,在列表为空时,列表取出一个元素),方法会抛出异常或者
 * 返回 null.
 */
public class NonBlockingThreadSafeList2 {
    public static void main(String... args) throws InterruptedException {
        ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>();
        Thread[] threads = new Thread[100];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new AddTask(list));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        PrintUtil.println("Main : Size of list is " + list.size());

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new PollTask(list));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        // 非阻塞式,此处返回了 null
        String last = list.poll();
        PrintUtil.println("Main : last poll " + last);
        PrintUtil.println("Main : Size of list is " + list.size());
    }
}

class AddTask implements Runnable {

    private ConcurrentLinkedDeque<String> list;

    AddTask(ConcurrentLinkedDeque<String> list) {
        this.list = list;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 10000; i++) {
            list.add(name + " : element " + i);
        }
    }
}

class PollTask implements Runnable {

    private ConcurrentLinkedDeque<String> list;

    PollTask(ConcurrentLinkedDeque<String> list) {
        this.list = list;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5000; i++) {
            list.pollFirst();
            list.pollLast();
        }
    }
}