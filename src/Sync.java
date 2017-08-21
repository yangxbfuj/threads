
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 使用 synchronized wait notifyAll 实现生产消费者模式
 */
public class Sync {
    public static void main(String... args) throws InterruptedException {
        EventStorage eventStorage = new EventStorage();
        Producer producer = new Producer(eventStorage);
        Consumer consumer = new Consumer(eventStorage);
        System.out.println("Start");
        ThreadGroup threadGroup = new ThreadGroup("Sync");
        for (int i = 0; i < 100; i++) {
            Thread producerThread = new Thread(threadGroup, producer);
            Thread consumerThread = new Thread(threadGroup, consumer);
            producerThread.start();
            consumerThread.start();
        }
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("End");
    }
}


class EventStorage {
    private int maxSize;
    private List<Date> storage;

    EventStorage() {
        maxSize = 10;
        storage = new LinkedList<>();
    }

    synchronized void set() {
        while (storage.size() == maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.add(new Date());
        System.out.printf("Set :%d\n", storage.size());
        notifyAll();
    }

    synchronized void get() {
        while (storage.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Get :%d : %s\n", storage.size(), ((LinkedList) storage).poll());
        notifyAll();
    }
}

class Producer implements Runnable {

    private EventStorage eventStorage;

    Producer(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    @Override
    public void run() {
        eventStorage.set();
    }
}

class Consumer implements Runnable {

    private EventStorage eventStorage;

    Consumer(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eventStorage.get();
    }
}