package com.rain.timelineview;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by HwanJ.Choi on 2018-5-29.
 */

public class ThreadTest {

    private boolean shouldMainRun;


    public void runMain() {
        synchronized (this) {
            while (!shouldMainRun) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 100; i++) {
                System.out.println("当前线程" + Thread.currentThread().getName());
            }
            shouldMainRun = false;
            this.notifyAll();
        }
    }

    public void runSub() {
        synchronized (this) {
            while (shouldMainRun) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 10; i++) {
                System.out.println("子线程" + Thread.currentThread().getName());
            }
            shouldMainRun = true;
            this.notifyAll();
        }
    }

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());

    public void test() {

        Runnable sub = new Runnable() {
            @Override
            public void run() {
                runSub();
            }
        };
        for (int i = 0; i < 50; i++) {
            threadPoolExecutor.execute(sub);
            runMain();
        }
    }

}
