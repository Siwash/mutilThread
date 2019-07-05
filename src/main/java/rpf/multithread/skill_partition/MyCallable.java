package rpf.multithread.skill_partition;

import org.springframework.data.redis.core.RedisTemplate;
import rpf.multithread.skill_redis.SkillService;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class MyCallable implements Callable <Boolean>{

    public static RedisTemplate<String,Object> redisTemplate;
    private  String key;
    private ReentrantLock lock;

    public MyCallable(String key, ReentrantLock lock) {
        this.key = key;
        this.lock = lock;
    }

    public static SkillService skillService;

    public MyCallable(String key) {
        this.key = key;
    }

    @Override
    public Boolean call() throws Exception {
        Long start=System.currentTimeMillis();
        boolean result= skillService.synchrSeckill(redisTemplate,key,lock);
        if (result){
            System.out.println("++++++++++++++++++++++++++++++++++++++参加了抢购,耗时："+(System.currentTimeMillis()-start));
        }
        return result;
    }
}
