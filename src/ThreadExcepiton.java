/**
 * Created by yangxb on 2017/8/17.
 */
public class ThreadExcepiton {
    public static void main(String... args) {
        MyThreadGroup myThreadGroup = new MyThreadGroup("MyThreadGroup");
        Thread thread = new Thread(myThreadGroup,
                () -> {
                    int num = Integer.parseInt("ABC");
                });
        //thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();
    }
}

class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("发生异常");
        System.out.println("当前线程为 " + Thread.currentThread().getName());
        System.out.println("异常线程为 " + t.getName());
        System.out.println("异常信息为 " + e.getMessage());
    }
}

class MyThreadGroup extends ThreadGroup {

    public MyThreadGroup(String name) {
        super(name);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("线程组：发生异常");
        System.out.println("线程组：当前线程为 " + Thread.currentThread().getName());
        System.out.println("线程组：异常线程为 " + t.getName());
        System.out.println("线程组：异常信息为 " + e.getMessage());
    }
}
