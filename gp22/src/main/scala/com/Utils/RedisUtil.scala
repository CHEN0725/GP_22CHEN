package com.Utils

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object RedisUtil {
    val config=new JedisPoolConfig
    config.setMaxTotal(20)
    config.setMaxIdle(10)
    //在这里搞一个redis的连接池，可以是懒加载的，不用就不加载，可以是私有的，通过方法来得到我的连接对象
    private lazy val Jpool = new JedisPool(config,"192.168.25.132",6379,10000)

    def getJedis():Jedis ={
        Jpool.getResource
    }

}
