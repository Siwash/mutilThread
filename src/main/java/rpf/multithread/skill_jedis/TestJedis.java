package rpf.multithread.skill_jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestJedis {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        Jedis jedis = new Jedis("127.0.0.1");
        jedis.set("count", "5");
        jedis.del("success", "fail");
        for (int i = 0; i < 10000; i++) {
                executor.execute(new MyRunnale("count"));
        }
            executor.shutdown();
    }

    static class MyRunnale implements Runnable {
        private Jedis jedis = new Jedis("127.0.0.1", 6379);

        public MyRunnale(String key) {
            this.key = key;
        }

        private String key;

        @Override
        public void run() {
            jedis.watch(key);
            int count=Integer.valueOf(jedis.get(key));
            String userId= UUID.randomUUID().toString();
            if (count<=0){
                jedis.unwatch();
                jedis.set("fail",userId);
                System.out.println("+++++++++++++++++++用户"+userId+"抢购失败");
            }else {
                Transaction tx=jedis.multi();
                tx.incrBy(key,-1);
                List<Object> list=tx.exec();
                if (list!=null){
                    jedis.sadd("success",userId);
                    System.out.println("-------------------用户"+userId+"抢购成功");
                }else {
                    jedis.set("fail",userId);
                    System.out.println("+++++++++++++++++++用户"+userId+"抢购失败");
                }
            }
        }
    }
}
