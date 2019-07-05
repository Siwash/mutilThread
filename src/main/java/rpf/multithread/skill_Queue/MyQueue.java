package rpf.multithread.skill_Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MyQueue {

    public  static int MAX_QUEUE=10;

    public  static  int GOODS_NUM=100;

    public BlockingQueue<Integer> queue;

    public MyQueue(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        BlockingQueue<Integer> queue=new ArrayBlockingQueue<Integer>(MAX_QUEUE);
        BlockingQueue<Integer> queue2=new ArrayBlockingQueue<Integer>(MAX_QUEUE);
        BlockingQueue<Integer> queue3=new ArrayBlockingQueue<Integer>(MAX_QUEUE);
        MyQueue myQueue = new MyQueue(queue);
        List<Future<Integer>> taskList=new ArrayList<>();
        ExecutorService executor= Executors.newFixedThreadPool(100);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < GOODS_NUM; i++) {
                    try {
                        System.out.println("第："+i+"次");
                        queue.put( 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    System.out.println("100件生产完成");
                    queue.put(-1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        for (int i = 0; i < 1000; i++) {
            Future<Integer> future = executor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    System.out.println("线程："+Thread.currentThread().getName()+" 抢购中……");
                    Integer value = queue.take();
                    if (value == -1) {
                        queue.put(-1);
                        System.out.println("线程："+Thread.currentThread().getName()+" 抢购失败，再接再厉！");
                    }else {
                        System.out.println("线程："+Thread.currentThread().getName()+" 抢购成功");
                    }

                    return value;
                }
            });
            taskList.add(future);
        }
        Integer count=0;
        for (Future<Integer> integerFuture : taskList) {
            count+=integerFuture.get()==-1? 0:1;
        }
        System.out.println("累计卖出："+count);
    }
}
