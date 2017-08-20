package chapter5;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

public class Fork2 {
    public static void main(String... args) throws InterruptedException {
        ProductListGenerator productListGenerator = new ProductListGenerator();
        List<Product> products = productListGenerator.generate(1000000);
        Task task = new Task(products, 0, products.size(), 0.20);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //forkJoinPool.invoke(task); invoke 将会阻塞调用线程直到任务完成
        forkJoinPool.execute(task);
        do {
            PrintUtil.println("Main : time         : " + new Date());
            PrintUtil.println("Main : Thread count : " + forkJoinPool.getActiveThreadCount());
            PrintUtil.println("Main : Thread steal : " + forkJoinPool.getStealCount());
            PrintUtil.println("Main : Parallelism  : " + forkJoinPool.getParallelism());
            TimeUnit.SECONDS.sleep(5);
        } while (!task.isDone());
        forkJoinPool.shutdown();

        if (task.isCompletedNormally()) {
            PrintUtil.println("Main : 任务正常执行完成");
        }
        PrintUtil.println("Main : 检验结果");
        for (Product product : products) {
            if (product.getPrice() != 12) {
                PrintUtil.println("Main 检测到错误,位置为 " + products.indexOf(product) + " 现在价格为 " + product.getPrice());
            }
        }
        PrintUtil.println("程序结束");
    }
}

class Product {
    private String name;
    private double price;

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    double getPrice() {
        return price;
    }

    void setPrice(double price) {
        this.price = price;
    }
}

class ProductListGenerator {
    List<Product> generate(int size) {
        List<Product> ret = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setPrice(10);
            ret.add(product);
        }
        return ret;
    }
}

class Task extends RecursiveAction {

    private static final long serialVersionUID = 1L;

    private List<Product> products;
    private int first;
    private int last;
    private double increment;

    Task(List<Product> products, int first, int last, double increment) {
        this.products = products;
        this.first = first;
        this.last = last;
        this.increment = increment;
    }

    @Override
    protected void compute() {
        if (last - first < 10) {
            updatePrice();
        } else {
            int middle = (last + first) / 2;
            //PrintUtil.println("Task : Pending task : " + getQueuedTaskCount());
            Task t1 = new Task(products, first, middle + 1, increment);
            Task t2 = new Task(products, middle + 1, last, increment);
            invokeAll(t1, t2);
        }
    }

    private void updatePrice() {
        for (int i = first; i < last; i++) {
            Product product = products.get(i);
            product.setPrice(product.getPrice() * (1 + increment));
        }
    }
}