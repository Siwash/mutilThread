package rpf.multithread.skill_redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component
public class SkillService {
    private   Logger LOG=LoggerFactory.getLogger(this.getClass());
    public boolean seckill(RedisTemplate<String,Object> redisTemplate,String  key){
            RedisLock lock=new RedisLock(redisTemplate,"mskey",10000,10000);
            try {
                Long start=System.currentTimeMillis();
                if (lock.lock()){
                    String  pronum=lock.get(key);
                    LOG.info("进来一个线程："+Thread.currentThread().getName());
                    if (Integer.parseInt(pronum)-1>=0){
                        lock.set(key,String.valueOf(Integer.parseInt(pronum)-1));
                        LOG.info("库存数量:"+(Integer.parseInt(pronum) -1)+"     成功!!!"+Thread.currentThread().getName());
                    }else {
                        LOG.info("已经被抢光了，请参与下轮抢购");
                    }
                    LOG.info("++++++++++++++++++++++++++++++++++++++参加了抢购,耗时："+(System.currentTimeMillis()-start));
                    return true;
                }else {
                    System.out.println("GGGGGGGGGGGGGGGGGGGGGGG");
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                //lock.unlock();
            }
            return  false;
    }
    public  boolean synchrSeckill(RedisTemplate<String, Object> redisTemplate, String key, ReentrantLock Rlock) throws InterruptedException {
        RedisLock lock=new RedisLock(redisTemplate,"mskey",10000,10000);
        Rlock.lockInterruptibly();
        String  pronum=lock.get(key);
        //Thread.sleep((int)(Math.random()*10));
        if (Integer.parseInt(pronum)>0){
            LOG.info("进来一个线程："+Thread.currentThread().getName());
            if (Integer.parseInt(pronum)-1>=0){
                lock.set(key,String.valueOf(Integer.parseInt(pronum)-1));
                LOG.info("库存数量:"+(Integer.parseInt(pronum) -1)+"     成功!!!"+Thread.currentThread().getName());
                Rlock.unlock();
                return true;
            }else {
                LOG.info("已经被抢光了，请参与下轮抢购");
                Rlock.unlock();
                return  false;
            }
        }
        Rlock.unlock();
        return false;
    }
}
