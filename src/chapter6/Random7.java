package chapter6;

import java.util.concurrent.ThreadLocalRandom;

import utils.PrintUtil;

/**
 * Java 7 引入 ThreadLocalRandom.
 * 它是线程本地变量.
 * 对于使用共享的 Random 对象来说,这种机制拥有更好的性能.
 */
public class Random7 {
    public static void main(String... args) {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new TaskLocalRandom());
            threads[i].start();
        }
    }
}

class TaskLocalRandom implements Runnable {

    TaskLocalRandom() {
        ThreadLocalRandom.current();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            PrintUtil.printf("%s : %d ", Thread.currentThread().getName(), ThreadLocalRandom.current().nextInt(10));
        }
    }
}
