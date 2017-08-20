package chapter6;

import org.omg.CORBA.TIMEOUT;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import utils.PrintUtil;

/**
 * 阻塞式列表在插入和删除时.如果列表为空,或者已满,操作不会立即执行,而是将
 * 调用这个操作的线程阻塞队列知道操作可以执行成功.
 * <p>
 * 引入 LinkedBlockingDeque
 */
public class LinkedBlockingDeque3 {
    public static void main(String... args) throws InterruptedException {
        LinkedBlockingDeque<String> list = new LinkedBlockingDeque<>(3);
        Client client = new Client(list);
        new Thread(client).start();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                String request = list.take();
                PrintUtil.printf("Main : Request : %s at %s, Size : %d", request, new Date(), list.size());
            }
            TimeUnit.MILLISECONDS.sleep(300);
        }
        PrintUtil.println("Main : End.");
    }
}

class Client implements Runnable {

    private LinkedBlockingDeque<String> requestList;

    Client(LinkedBlockingDeque<String> requestList) {
        this.requestList = requestList;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                StringBuffer request = new StringBuffer();
                request.append(i).append(" : ").append(j);
                try {
                    requestList.put(request.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PrintUtil.printf("Client : %s at %s", request, new Date());
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        PrintUtil.println("Client : End.");
    }
}