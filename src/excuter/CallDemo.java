package excuter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Callable and Future
 */
public class CallDemo {
    public static void main(String... args) throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<FactorialCalculator> factorialCalculators = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Integer number = Math.abs(random.nextInt(10));
            FactorialCalculator factorialCalculator = new FactorialCalculator(number);
            Future<Integer> result = executor.submit(factorialCalculator);
            factorialCalculator.setResult(result);
            factorialCalculators.add(factorialCalculator);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("任务结果:");
        for (FactorialCalculator calculator : factorialCalculators) {
            Integer num = null;
            try {
                num = calculator.getResult().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.printf("Main : 任务 %d 结果为 %d! = %d\n", factorialCalculators.indexOf(calculator), calculator.getNumber(), num);
        }

    }
}

/**
 * 阶乘
 */
class FactorialCalculator implements Callable<Integer> {

    private Integer number;
    private Future<Integer> result;

    FactorialCalculator(Integer number) {
        this.number = number;
    }

    void setResult(Future<Integer> result) {
        this.result = result;
    }

    Future<Integer> getResult() {
        return result;
    }

    Integer getNumber() {
        return number;
    }

    @Override
    public Integer call() throws Exception {
        return factorial(number);
    }

    private int factorial(int num) {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (num == 0) {
            return 1;
        } else if (num > 0) {
            return num * factorial(num - 1);
        } else {
            throw new IllegalArgumentException("num 必须为自然数");
        }
    }
}