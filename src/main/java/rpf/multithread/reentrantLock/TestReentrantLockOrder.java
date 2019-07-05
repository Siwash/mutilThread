package rpf.multithread.reentrantLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class TestReentrantLockOrder {
    public static void main(String[] args) {
        testForEach();
        //testManual();
    }

    private static void testForEach() {
        ExecutorService executorService= Executors.newFixedThreadPool(100);
        ReentrantLock lock = new ReentrantLock(true);
        int count =0;
//        while (count>=0){
//            count--;
        for (int i = 0; i < 4; i++) {
            final  char thread='A';
            final  int index=i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        lock.lock();
                        System.out.println("正在运行线程："+index);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lock.unlock();
                    }

                }
            });
        }
//        }
        executorService.shutdown();
    }
    public  static  void testManual(){
        ExecutorService executorService= Executors.newFixedThreadPool(5);
        ReentrantLock lock = new ReentrantLock();
        for (int i=0;i<1;i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    System.out.println("正在运行线程：A");
                    lock.unlock();
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    System.out.println("正在运行线程：B");
                    lock.unlock();
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    System.out.println("正在运行线程：C");
                    lock.unlock();
                }
            });
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    System.out.println("正在运行线程：D");
                    lock.unlock();
                }
            });
        }
        executorService.shutdown();
    }
}
