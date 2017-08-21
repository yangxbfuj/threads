import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yangxb on 2017/8/17.
 */
public class LockDemo {

    public static void main(String... args) {
        PrintQueue printQueue = new PrintQueue();
        for (int i = 1; i < 10; i++) {
            new Thread(new Job(printQueue)).start();
        }
    }
}

class PrintQueue {

    private final Lock lock = new ReentrantLock();

    void printJob(Object document) {
        lock.lock();
        try {
            Long duration = (long) (Math.random() * 10000);
            System.out.println("当前线程为:" + Thread.currentThread().getName() + "打印耗时为 " + duration);
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}

class Job implements Runnable {

    private PrintQueue printQueue;

    Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    @Override
    public void run() {
        System.out.println("线程 " + Thread.currentThread().getName() + " 要打印");
        printQueue.printJob(new Object());
        System.out.println("线程 " + Thread.currentThread().getName() + " 完成打印");
    }
}
