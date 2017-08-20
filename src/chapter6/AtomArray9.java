package chapter6;

import java.util.concurrent.atomic.AtomicIntegerArray;

import utils.PrintUtil;

/**
 * Compare-and-Swap Operation 比较和交换操作
 * 1.取得变量的值
 * 2.在一个临时变量中修改变量的值.即变量的新值
 * 3.如果上面获得的变量旧值与当前变量的值相等,就用新值替换就值.如果已有其他线程修改了这个变量的值,上面获得的变量的旧
 * 值就可能与当前变量值不同.
 */
public class AtomArray9 {
    public static void main(String... args) throws InterruptedException {
        final int THREADS = 100;
        AtomicIntegerArray vector = new AtomicIntegerArray(1000);
        Incrementer incrementer = new Incrementer(vector);
        Decrementor decrementor = new Decrementor(vector);
        Thread[] threadsIncrementer = new Thread[THREADS];
        Thread[] threadsDecrementor = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threadsIncrementer[i] = new Thread(incrementer);
            threadsDecrementor[i] = new Thread(decrementor);
            threadsIncrementer[i].start();
            threadsDecrementor[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            threadsIncrementer[i].join();
            threadsDecrementor[i].join();
        }
        for (int i = 0; i < vector.length(); i++) {
            if (vector.get(i) != 0) {
                PrintUtil.printf("vector[%d] != 0 ", i);
            }
        }
        PrintUtil.println("End.");
    }
}

class Incrementer implements Runnable {

    private AtomicIntegerArray vector;

    Incrementer(AtomicIntegerArray vector) {
        this.vector = vector;
    }

    @Override
    public void run() {
        for (int i = 0; i < vector.length(); i++) {
            vector.getAndIncrement(i);
        }
    }
}

class Decrementor implements Runnable {

    private AtomicIntegerArray vector;

    Decrementor(AtomicIntegerArray vector) {
        this.vector = vector;
    }

    @Override
    public void run() {
        for (int i = 0; i < vector.length(); i++) {
            vector.getAndDecrement(i);
        }
    }
}