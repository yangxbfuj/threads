package single;

/**
 * 单例测试
 */
public class Singleton {

    private static Singleton instance;

    private Singleton() {
        System.out.println("初始化");
    }

    private static int num = 0;

    private static void add() {
        num++;
    }

    public static void main(String... args) throws InterruptedException {
        int i = 1000;
        Thread threads[] = new Thread[i];
        while (i-- > 0) threads[i] = new Thread(Singleton::add);
        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) thread.join();
        System.out.println(num);
    }

    public static Singleton getInstance() {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
            return instance;
        }
    }
}
