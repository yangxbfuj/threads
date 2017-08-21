package chapter7;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import utils.PrintUtil;

public class CustomLock9 {
    public static void main(String... args) {
        MyLock lock = new MyLock();
        for (int i = 0; i < 10; i++) {
            Task9 task9 = new Task9(lock, " - " + i);
            new Thread(task9).start();
        }
        boolean value;
        do {
            try {
                value = lock.tryLock(1, TimeUnit.SECONDS);
                if (!value) {
                    PrintUtil.printf("Main: try to get lock");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                value = false;
            }
        } while (!value);
        PrintUtil.printf("Main: Got the lock");
        lock.unlock();
        PrintUtil.printf("Main: End.");
    }
}

class MyAbstractQueueSynchronizer extends AbstractQueuedSynchronizer {

    private AtomicInteger state;

    MyAbstractQueueSynchronizer() {
        state = new AtomicInteger(0);
    }

    @Override
    protected boolean tryAcquire(int arg) {
        return state.compareAndSet(0, 1);
    }

    @Override
    protected boolean tryRelease(int arg) {
        return state.compareAndSet(1, 0);
    }
}

class MyLock implements Lock {

    private AbstractQueuedSynchronizer sync;

    MyLock() {
        sync = new MyAbstractQueueSynchronizer();
    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        try {
            return sync.tryAcquireNanos(1, 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, TimeUnit.NANOSECONDS.convert(time, unit));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.new ConditionObject();
    }
}

class Task9 implements Runnable {

    private MyLock lock;
    private String name;

    Task9(MyLock lock, String name) {
        this.lock = lock;
        this.name = name;
    }

    @Override
    public void run() {
        lock.lock();
        PrintUtil.printf("Task: %s :take the lock.", name);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            PrintUtil.printf("Task: %s :free the lock.", name);
            lock.unlock();
        }
    }
}