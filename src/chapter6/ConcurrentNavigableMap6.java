package chapter6;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import utils.PrintUtil;

public class ConcurrentNavigableMap6 {
    public static void main(String... args) throws InterruptedException {
        ConcurrentSkipListMap<String, Contact> map = new ConcurrentSkipListMap<>();
        Thread[] threads = new Thread[25];
        int counter = 0;
        for (char i = 'A'; i < 'Z'; i++) {
            Task6 task6 = new Task6(map, String.valueOf(i));
            threads[counter] = new Thread(task6);
            threads[counter].start();
            counter++;
        }
        for (Thread thread : threads) thread.join();
        PrintUtil.printf("Main : Size of the map : %d", map.size());
        Map.Entry<String, Contact> elment;
        Contact contact;
        elment = map.firstEntry();
        contact = elment.getValue();
        PrintUtil.printf("Main : First Entry is %s : %s", contact.getName(), contact.getPhone());

        elment = map.lastEntry();
        contact = elment.getValue();
        PrintUtil.printf("Main : Last  Entry is %s : %s", contact.getName(), contact.getPhone());

        // 非阻塞式,此处返回 null
        contact = map.get("Z");
        if (contact == null)
            PrintUtil.printf("Main : Last  Entry is %s : %s", null, null);
    }
}

class Contact {

    private String name;
    private String phone;

    Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}

class Task6 implements Runnable {

    private ConcurrentSkipListMap<String, Contact> map;
    private String id;

    Task6(ConcurrentSkipListMap<String, Contact> map, String id) {
        this.id = id;
        this.map = map;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            Contact contact = new Contact(id, String.valueOf(i + 10000));
            map.put(id + contact.getPhone(), contact);
        }
    }
}