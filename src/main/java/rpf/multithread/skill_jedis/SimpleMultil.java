package rpf.multithread.skill_jedis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleMultil {
    public static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public static void main(String[] args) {
        jedis.set("count", "100");
        SimpleMultil simpleMultil = new SimpleMultil();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 100; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Long start=System.currentTimeMillis();
                   if (simpleMultil.secKill("count")){
                    System.out.println("耗时："+(System.currentTimeMillis()-start));
                   }
                }
            });
        }
        executor.shutdown();
    }

    public synchronized boolean secKill(String key) {
        int count=Integer.parseInt(jedis.get(key));
        if (count>=0){
            System.out.println("用户："+Thread.currentThread().getName()+"抢购成功"+" 目前库存数量："+(count-1));
            jedis.set(key,String.valueOf(count-1));
            return true;
        }

        return false;
    }
}
