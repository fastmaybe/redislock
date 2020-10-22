package com.redis.fast.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: liulang
 * @Date: 2020/10/12 15:03
 */
@Configuration
public class RedisConfig {

//    @Bean
//    @SuppressWarnings("unchecked")
//    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
//        RedisTemplate template = new RedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory);
//        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
////        template.setDefaultSerializer(new GenericFastJsonRedisSerializer());
//        return template;
//    }

    @Bean
    @SuppressWarnings("unchecked")
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        //配置连接工厂
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis 的value值
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();

        //指定要序列化的域，field，get和set，以及修饰符范围
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        //指定序列化输入的类型，类必须是非final类
//        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
//                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //序列化配置为String格式
        template.setValueSerializer(new StringRedisSerializer());

        //
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
