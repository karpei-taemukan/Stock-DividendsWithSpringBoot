package com.zerobase.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@RequiredArgsConstructor
@Configuration
public class CacheConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    /*
     *   Redis 서버 구축 방법
     *
     *   Single : 단일 노드로 구성
     *
     *   마스터-슬레이브 모드로 구성
     *   Sentinel
     *   Cluster
     *
     * */

    // RedisClusterConfiguration  Cluster 서버
    //RedisSentinelConfiguration Sentinel 서버

    /*
    * cache 사용 여부 생각
    * -> 요청이 자주 들어오는가?
    * -> 자주 변경되는 데이터인가?
    *
    *
    * cache 를 삭제해야하는 이유
    *
    * 1) cache 에 데이터 D1이 있고,
    * D1에 해당하는 키를 가진 데이터가 업데이트된 경우,
    * 클라이언트가 요청을 하면 업데이트 안된 cache 에 있는 데이터부터 가져온다
    * => 해당 cache 를 비워주거나 혹은 cache 내용도 업데이트한다
    *
    * 2) cache 도 저장하는 용량의 한계가 있음
    *   => 오래 저장된 데이터나, 사용 빈도가 적은 데이터는 cache 에서 삭제
    * */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(); // single 서버

        conf.setHostName(this.host);
        conf.setPort(this.port);
        return new LettuceConnectionFactory(conf); //redisConnectionFactory bean 사용
    }


    @Bean
    public CacheManager redisCacheManager (RedisConnectionFactory redisConnectionFactory){

        RedisCacheConfiguration conf
                = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
                /* .entryTtl(Duration.of(~~~)) : redis 전체 데이터에 대한 TTL(Time To Live) 설정  */
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();
    }
}
