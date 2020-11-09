package com.example.demo.dto;

/**
 * @author huang
 * @create 2020/10/30
 */
public class ThreadLocalMain {

    private static ThreadLocal<Container> tl = new ThreadLocal<>();
    static class Container {
        int num;
    }

    public static void main(String[] args) throws InterruptedException {

        tl.set(new Container());


        Container container = tl.get();
        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                container.num++;
            }
        };


        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(tl.get().num);
    }
}
