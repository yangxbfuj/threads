import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yangxb on 2017/8/17.
 */
public class RWLockDemo {

    public static void main(String... args) {
        PricesInfo pricesInfo = new PricesInfo();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            threads.add(new Thread(new Reader(pricesInfo)));
        }
        threads.add(new Thread(new Writer(pricesInfo)));
        for (Thread thread : threads) thread.start();
    }
}

class PricesInfo {

    private double price1;
    private double price2;

    private ReadWriteLock readWriteLock;

    PricesInfo() {
        price1 = 0.0;
        price2 = 0.0;
        readWriteLock = new ReentrantReadWriteLock();
    }

    double getPrice1() {
        readWriteLock.readLock().lock();
        double value = price1;
        System.out.println("线程 " + Thread.currentThread().getName() + " price1 = " + value);
        readWriteLock.readLock().unlock();
        return value;
    }

    double getPrice2() {
        readWriteLock.readLock().lock();
        double value = price2;
        //System.out.println("线程 " + Thread.currentThread().getName() + " price2 = " + value);
        readWriteLock.readLock().unlock();
        return value;
    }

    void setPrice(double price1, double price2) {
        readWriteLock.writeLock().lock();
        System.out.println("尝试改变数据");
        this.price1 = price1;
        this.price2 = price2;
        System.out.println("已经改变数据");
        readWriteLock.writeLock().unlock();
    }

}

class Reader implements Runnable {

    private PricesInfo pricesInfo;

    Reader(PricesInfo pricesInfo) {
        this.pricesInfo = pricesInfo;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            pricesInfo.getPrice1();
            pricesInfo.getPrice2();
        }
    }
}

class Writer implements Runnable {

    private PricesInfo pricesInfo;

    Writer(PricesInfo pricesInfo) {
        this.pricesInfo = pricesInfo;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            pricesInfo.setPrice(Math.random() * 10, Math.random() * 8);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}