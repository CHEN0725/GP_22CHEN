package com.ETL

import com.Utils.RedisUtil
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis

/**
  * app字典文件清洗，取出appname
  */
object AppToRedis {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setAppName(this.getClass.getName)
      .setMaster("local[*]")
    val sc=new SparkContext(conf)
    val lines=sc.textFile("dir/app_dict.txt")
    val appInfo = lines.map(line => line.split("\\s")).filter(_.length>=5).map(x=>{
      (x(4),x(1))
    })
    //println(appInfo.collect.toList)
    val jedis= RedisUtil.getJedis()
   appInfo.collectAsMap().foreach(x=>{
     jedis.set(x._1,x._2)
   })
    jedis.close()


  }
}
