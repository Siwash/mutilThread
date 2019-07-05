package rpf.multithread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import rpf.multithread.skill_partition.MyCallable;
import rpf.multithread.skill_redis.MyThread;
import rpf.multithread.skill_redis.RedisLock;
import rpf.multithread.skill_redis.SkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class SkillRedisApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SkillRedisApplication.class, args);
        RedisTemplate<String,Object> redisTemplate= (RedisTemplate<String, Object>) context.getBean("redisTemplate");
        int date=10000;
        int size=50;
        Jedis jedis=new Jedis("127.0.0.1",6379);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                StringRedisSerializer serializer = new StringRedisSerializer();
                redisConnection.set(serializer.serialize("pronum"), serializer.serialize(String.valueOf(date)));
                return serializer;
            }
        });
        List<FutureTask<Boolean>>  taskList=new ArrayList<>();
        ExecutorService executor= Executors.newFixedThreadPool(500);
        SkillService service=new SkillService();
        MyCallable.skillService=service;
        MyCallable.redisTemplate=redisTemplate;
        int count=1;
        Long start=System.currentTimeMillis();
        //按照size为长度，划分成任务快
        for (int j=0;j<date;j+=size){
            String key="pronum"+count++;
            jedis.set(key,String.valueOf(Math.min(date-j+1,size)));
            ReentrantLock reentrantLock=new ReentrantLock();
            //每个size中有size*2个线程去抢购
            for (int i=0;i<size*2;i++){
                FutureTask<Boolean> task= (FutureTask<Boolean>) executor.submit(new MyCallable(key,reentrantLock));
                        taskList.add(task);
                }
          }
          int success=0;
        for (FutureTask<Boolean> futureTask : taskList) {
            try {
                success+=(boolean)futureTask.get()? 1:0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println(success);
        if (success==10000){
            System.out.println("_______----耗时："+(System.currentTimeMillis()-start)+"----_________");
        }
        executor.shutdown();

//        SkillService service=new SkillService();
//        for (int i = 0; i < 1000; i++) {
//            Thread task=new MyThread(service,redisTemplate,"msKey");
//            task.start();
//        }
    }
}
