package com.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@RestController
public class RedisController {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/deduct_stack")
    public String deductStack() throws Exception{
        String docker_result = "product_001";
        try{
            Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(docker_result, "yangshaojie");
            stringRedisTemplate.expire(docker_result,10, TimeUnit.SECONDS);
            if (!absent){
                return "error";
            }
            /*RLock redissonLock = redisson.getLock(docker_result);
            redissonLock.lock(30,TimeUnit.SECONDS);*/
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0 ){
                int realStack = stock - 1;
                stringRedisTemplate.opsForValue().set("stock",realStack + "");
                System.out.println("扣减成功,剩余库存:"+realStack+"");
            }else {
                System.out.println("扣减失败,库存不足");
            }
        }finally {
            stringRedisTemplate.delete(docker_result);
        }
        return "end";
    }
}
