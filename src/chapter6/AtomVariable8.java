package chapter6;

import java.util.concurrent.atomic.AtomicLong;

import utils.PrintUtil;

/**
 * CAS(Compare And Set) 原子操作
 */
public class AtomVariable8 {
    public static void main(String... args) throws InterruptedException {
        Account account = new Account();
        account.setBalance(1000);
        Company company = new Company(account);
        Thread companyThread = new Thread(company);
        Bank bank = new Bank(account);
        Thread bankThread = new Thread(bank);
        PrintUtil.println("开始 余额为 " + account.getBalance());
        companyThread.start();
        bankThread.start();
        companyThread.join();
        bankThread.join();
        PrintUtil.println("结束 余额为 " + account.getBalance());
    }
}

class Account {

    private AtomicLong balence;

    Account() {
        balence = new AtomicLong();
    }

    long getBalance() {
        return balence.get();
    }

    void setBalance(long balence) {
        this.balence.getAndSet(balence);
    }

    void addAmount(long amount) {
        this.balence.getAndAdd(amount);
    }

    void subtractAmount(long amount) {
        this.balence.getAndAdd(-amount);
    }
}

class Company implements Runnable {

    private Account account;

    Company(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            account.addAmount(1000);
        }
    }
}

class Bank implements Runnable {

    private Account account;

    Bank(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            account.subtractAmount(1000);
        }
    }
}