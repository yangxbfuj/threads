package excuter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用 invokeAny 对比计算斐波那契数列的两种算法的速度
 */
public class FibTwoWays {
    public static void main(String... args) throws InterruptedException, ExecutionException {
        for (int i = 1; i < 30; i++) {
            System.out.printf("计算 Fib(%d)", i);
            List<Callable<Result>> list = new ArrayList<>();
            list.add(new FibTask(i));
            list.add(new FibWithDynamicData(i));
            ExecutorService service = Executors.newCachedThreadPool();
            Result result = service.invokeAny(list);
            service.shutdown();
            System.out.println(result);
        }
    }
}

class Fib {

    private int num;
    private int[] dynamicData;

    Fib(int num) {
        if (num <= 0) throw new IllegalArgumentException("参数必须大于0");
        this.num = num;
        dynamicData = new int[num];
    }

    int fib() {
        return fib(num);
    }

    int fibWithDynamic() {
        return fibWithDynamic(num);
    }

    private int fib(int num) {
        if (num <= 0) throw new IllegalArgumentException("参数必须大于0");
        else if (num == 1) return 1;
        else if (num == 2) return 1;
        else return fib(num - 1) + fib(num - 2);
    }

    private int fibWithDynamic(int num) {
        if (num < 0) throw new IllegalArgumentException("参数必须大于0");
        else if (num == 1) return 1;
        else if (num == 2) return 1;
        else {
            if (dynamicData[num - 1] == 0) dynamicData[num - 1] = fibWithDynamic(num - 1) + fibWithDynamic(num - 2);
            return dynamicData[num - 1];
        }
    }
}

class FibTask implements Callable<Result> {

    private int num;

    FibTask(int num) {
        this.num = num;
    }

    @Override
    public Result call() throws Exception {
        return new Result("FibTask", new Fib(num).fib());
    }
}

class FibWithDynamicData implements Callable<Result> {

    private int num;

    FibWithDynamicData(int num) {
        this.num = num;
    }

    @Override
    public Result call() throws Exception {
        return new Result("FibWithDynamicData", new Fib(num).fibWithDynamic());
    }
}

class Result {
    private String taskName;
    private int result;

    Result(String taskName, int result) {
        this.taskName = taskName;
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("%s 完成 结果为 %d", taskName, result);
    }
}