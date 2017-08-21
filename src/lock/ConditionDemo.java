package lock;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yangxb on 2017/8/17.
 */
public class ConditionDemo {
    public static void main(String... args) throws InterruptedException {
        FileMock fileMock = new FileMock(100, 10);
        Buffer buffer = new Buffer(20);
        Producer producer = new Producer(fileMock, buffer);
        Thread threadProducer = new Thread(producer);

        Consumer consumers[] = new Consumer[3];
        Thread threadsConsumers[] = new Thread[3];
        for (int i = 0; i < 3; i++) {
            consumers[i] = new Consumer(buffer);
            threadsConsumers[i] = new Thread(consumers[i], "Consumer " + i);
        }

        threadProducer.start();
        for (Thread thread : threadsConsumers) {
            thread.start();
        }
        threadProducer.join();
        for (Thread thread : threadsConsumers) {
            thread.join();
        }
    }
}

class FileMock {

    private String content[];
    private int index;

    public FileMock(int size, int length) {
        content = new String[size];
        for (int i = 0; i < size; i++) {
            StringBuffer buffer = new StringBuffer(length);
            for (int j = 0; j < length; j++) {
                char indice = (char) (Math.random() * 255);
                buffer.append(indice);
            }
            content[i] = buffer.toString();
        }
        index = 0;
    }

    boolean hasMoreLines() {
        return index < content.length;
    }

    String getLine() {
        if (this.hasMoreLines()) {
            System.out.println("Mock: " + (content.length - index));
            return content[index++];
        }
        return null;
    }
}

class Buffer {
    private LinkedList<String> buffer;
    private int maxSize;
    private ReentrantLock lock;
    private Condition lines;
    private Condition space;
    private boolean pendingLines;

    Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        lines = lock.newCondition();
        space = lock.newCondition();
        pendingLines = true;
    }

    void insert(String line) {
        lock.lock();
        try {
            while (buffer.size() == maxSize) {
                space.await();
            }
            buffer.offer(line);
            System.out.printf("线程 %s 插入 %d\n", Thread.currentThread().getName(), buffer.size());
            lines.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    String get() {
        String line = null;
        lock.lock();
        try {
            while ((buffer.size() == 0) && hasPendingLines()) {
                lines.await();
            }
            if (hasPendingLines()) {
                line = buffer.poll();
                System.out.printf("线程 %s 读取 %d\n", Thread.currentThread().getName(), buffer.size());
                space.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return line;
    }

    void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }

    boolean hasPendingLines() {
        return pendingLines || buffer.size() > 0;
    }
}

class Producer implements Runnable {

    private FileMock mock;
    private Buffer buffer;

    Producer(FileMock fileMock, Buffer buffer) {
        this.mock = fileMock;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        buffer.setPendingLines(true);
        while (mock.hasMoreLines()) {
            String line = mock.getLine();
            buffer.insert(line);
        }
        buffer.setPendingLines(false);
    }
}

class Consumer implements Runnable {

    private Buffer buffer;

    Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (buffer.hasPendingLines()) {
            processLine(buffer.get());
        }
    }

    private void processLine(String line) {
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}