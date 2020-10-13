package com.company;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SavingsAccount {
    private int balance;
    private Lock lock;
    private Condition condition;
    private int withdrawPreferredWaiting;

    public SavingsAccount(int balance){
        this.balance = balance;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        withdrawPreferredWaiting = 0;
    }

    public void deposit(int k){
        lock.lock();
        try {
            balance += k;
            System.out.println("deposit: \t\t\t+ " + k + ", balance = " + balance);
        } finally {
            condition.signalAll();
            lock.unlock();
        }
    }

    public void withdraw(int k){
        lock.lock();
        try {
            while (balance < k || withdrawPreferredWaiting > 0) {
                System.out.println("ordinary await...");
                condition.await();
            }
            balance -= k;
            System.out.println("ordinary withdraw: \t- " + k + ", balance = " + balance);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void withdrawPreferred(int k){
        lock.lock();
        try {
            withdrawPreferredWaiting++;
            while (balance < k) {
                System.out.println("preferred await...");
                condition.await();
            }
            balance -= k;
            System.out.println("preferred withdraw: - " + k + ", balance = " + balance);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            withdrawPreferredWaiting--;
            condition.signalAll();
            lock.unlock();
        }
    }

    public void transfer(int k, SavingsAccount reserve) {
        lock.lock();
        try {
            reserve.withdraw(k);
            deposit(k);
            System.out.println("Transfer was successful!!!");
        } finally {
            lock.unlock();
        }
    }
}
